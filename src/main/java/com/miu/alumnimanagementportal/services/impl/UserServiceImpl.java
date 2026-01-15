package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.entities.Profile;

import com.miu.alumnimanagementportal.dtos.*;
import com.miu.alumnimanagementportal.entities.Role;
import com.miu.alumnimanagementportal.entities.User;
import com.miu.alumnimanagementportal.exceptions.BadRequestException;
import com.miu.alumnimanagementportal.exceptions.DataAlreadyExistException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.RoleRepository;
import com.miu.alumnimanagementportal.repositories.UserRepository;
import com.miu.alumnimanagementportal.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final Converter converter;
    private final PasswordEncoder passwordEncoder;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCK_MINUTES = 15;

    @Override
    @Transactional
    public UserDto register(UserDto userDto) {
        if (userDto == null) throw new BadRequestException("User data is required");

        // Email uniqueness check - ONE email = ONE user
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new BadRequestException("Email is required");
        }
        
        User existing = repository.findByEmail(userDto.getEmail());
        if (existing != null) {
            throw new DataAlreadyExistException("User with email " + userDto.getEmail() + " already exists. One email can only correspond to one user account.");
        }

        // if id is provided and already exists -> conflict
        if (userDto.getId() != null && repository.existsById(userDto.getId())) {
            throw new DataAlreadyExistException("User with id " + userDto.getId() + " already exists");
        }

        User user = converter.convert(userDto, User.class);

        // Validate and set role - ONE user = ONE role
        if (userDto.getRole() == null || userDto.getRole().getTitle() == null) {
            throw new BadRequestException("Role is required. User must have exactly one role: ADMIN, ALUMNI, or STUDENT.");
        }
        
        String roleTitle = userDto.getRole().getTitle().toUpperCase().trim();
        if (!roleTitle.equals("ADMIN") && !roleTitle.equals("ALUMNI") && !roleTitle.equals("STUDENT")) {
            throw new BadRequestException("Invalid role. Role must be ADMIN, ALUMNI, or STUDENT only.");
        }
        
        Role role = roleRepository.findByTitleIgnoreCase(roleTitle);
        if (role == null) {
            role = new Role();
            role.setTitle(roleTitle);
            role = roleRepository.save(role);
        }
        user.setRole(role);

        // encode password if present
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        User saved = repository.save(user);
        return converter.convert(saved, UserDto.class);
    }

    @Override
    @Transactional
    public List<UserDto> findAll() {
        List<User> users = repository.findAll();
        // Ensure all users have valid roles (migration support for existing data)
        users.forEach(user -> {
            if (user.getRole() == null) {
                Role defaultRole = roleRepository.findByTitleIgnoreCase("STUDENT");
                if (defaultRole == null) {
                    defaultRole = new Role();
                    defaultRole.setTitle("STUDENT");
                    defaultRole = roleRepository.save(defaultRole);
                }
                user.setRole(defaultRole);
                repository.save(user);
            }
        });
        return converter.convertList(users, UserDto.class);
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Long id) {
        if (id == null) throw new BadRequestException("User id is required");
        if (userDto == null) throw new BadRequestException("User data is required");

        User existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        // Ensure user has a valid role (migration support)
        if (existing.getRole() == null) {
            Role defaultRole = roleRepository.findByTitleIgnoreCase("STUDENT");
            if (defaultRole == null) {
                defaultRole = new Role();
                defaultRole.setTitle("STUDENT");
                defaultRole = roleRepository.save(defaultRole);
            }
            existing.setRole(defaultRole);
        }

        // Update only provided fields, preserve others
        if (userDto.getFirstName() != null) existing.setFirstName(userDto.getFirstName());
        if (userDto.getLastName() != null) existing.setLastName(userDto.getLastName());
        
        // Email uniqueness check - ensure no duplicate emails
        if (userDto.getEmail() != null && !userDto.getEmail().equals(existing.getEmail())) {
            User emailCheck = repository.findByEmail(userDto.getEmail());
            if (emailCheck != null && !emailCheck.getId().equals(existing.getId())) {
                throw new DataAlreadyExistException("Email " + userDto.getEmail() + " is already in use by another user.");
            }
            existing.setEmail(userDto.getEmail());
        }
        
        // only encode if password provided and not already hashed (otherwise keep existing)
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            String password = userDto.getPassword();
            // Check if password is already hashed (BCrypt hashes start with $2a$, $2b$, or $2y$)
            // This prevents double-encoding if the hashed password is accidentally sent back
            if (!password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
                existing.setPassword(passwordEncoder.encode(password));
            }
            // If password is already hashed, ignore it (don't update)
        }
        
        // Update profile if provided
        if (userDto.getProfile() != null) {
            ProfileDto profileDto = userDto.getProfile();
            if (existing.getProfile() != null) {
                // Update existing profile - update only provided fields, preserve collections
                Profile existingProfile = existing.getProfile();
                
                // Update basic fields if provided (allow empty strings to clear fields)
                if (profileDto.getPhone() != null) existingProfile.setPhone(profileDto.getPhone());
                if (profileDto.getProfileImage() != null) existingProfile.setProfileImage(profileDto.getProfileImage());
                if (profileDto.getDepartment() != null) existingProfile.setDepartment(profileDto.getDepartment());
                if (profileDto.getPassingYear() != null) existingProfile.setPassingYear(profileDto.getPassingYear());
                if (profileDto.getJobTitle() != null) existingProfile.setJobTitle(profileDto.getJobTitle());
                if (profileDto.getCvLink() != null) existingProfile.setCvLink(profileDto.getCvLink());
                if (profileDto.getPortfolioLink() != null) existingProfile.setPortfolioLink(profileDto.getPortfolioLink());
                
                // Preserve collections - only update if explicitly provided in DTO
                // If collections are null in DTO, they are not being updated, so preserve existing
                // Collections are managed separately through their own endpoints
            } else {
                // Create new profile
                Profile newProfile = converter.convert(profileDto, Profile.class);
                newProfile.setUser(existing); // Set user relationship
                existing.setProfile(newProfile);
            }
        }
        
        // Preserve role - don't update role when updating profile
        // Role should be managed separately via updateRole method

        User saved = repository.save(existing);
        return converter.convert(saved, UserDto.class);
    }

    @Override
    @Transactional
    public UserDto getUserById(Long id) {
        if (id == null) throw new BadRequestException("User id is required");

        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        // Ensure user has a valid role (migration support)
        if (user.getRole() == null) {
            Role defaultRole = roleRepository.findByTitleIgnoreCase("STUDENT");
            if (defaultRole == null) {
                defaultRole = new Role();
                defaultRole.setTitle("STUDENT");
                defaultRole = roleRepository.save(defaultRole);
            }
            user.setRole(defaultRole);
            repository.save(user);
        }

        return converter.convert(user, UserDto.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) throw new BadRequestException("User id is required");
        if (!repository.existsById(id)) throw new ResourceNotFoundException("User with id " + id + " not found");
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public UserActivationDto update(UserActivationDto dto, Long id) {
        if (id == null) throw new BadRequestException("User id is required");
        if (dto == null) throw new BadRequestException("Activation data is required");

        User existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        existing.set_active(dto.is_active());

        User saved = repository.save(existing);
        return new UserActivationDto(saved.getId(), saved.is_active());
    }

    @Override
    @Transactional
    public void login(SigninDto signinDto) {
        if (signinDto == null) throw new BadRequestException("Signin data is required");
        if (signinDto.getEmail() == null || signinDto.getEmail().isBlank()) throw new BadRequestException("Email is required");
        if (signinDto.getPassword() == null) throw new BadRequestException("Password is required");
        if (signinDto.getRole() == null || signinDto.getRole().isBlank()) throw new BadRequestException("Role is required");

        User user = Optional.ofNullable(repository.findByEmail(signinDto.getEmail()))
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + signinDto.getEmail() + " not found"));

        // Ensure user has a valid role - fix if missing (migration support)
        if (user.getRole() == null) {
            // Assign default STUDENT role if missing (for existing users)
            Role defaultRole = roleRepository.findByTitleIgnoreCase("STUDENT");
            if (defaultRole == null) {
                defaultRole = new Role();
                defaultRole.setTitle("STUDENT");
                defaultRole = roleRepository.save(defaultRole);
            }
            user.setRole(defaultRole);
            repository.save(user);
        }

        // CRITICAL: Validate that the user's role matches the login role
        // ONE email = ONE user = ONE role - users can ONLY login with their registered role
        String requestedRole = signinDto.getRole().toUpperCase().trim();
        String userRole = user.getRole().getTitle().toUpperCase().trim();
        
        if (!requestedRole.equals(userRole)) {
            throw new BadRequestException("Invalid login attempt. This email is registered as " + userRole + 
                    ". You cannot login as " + requestedRole + ". Please login with the correct role or register a new account with a different email.");
        }

        // already locked?
        if (user.is_locked()) {
            // unlock if lock duration passed
            LocalDateTime lastLocked = user.getLastLockedDateTime();
            if (lastLocked != null) {
                long minutes = Duration.between(lastLocked, LocalDateTime.now()).toMinutes();
                if (minutes >= LOCK_MINUTES) {
                    user.set_locked(false);
                    user.setLoginCount(0);
                } else {
                    throw new BadRequestException("User is locked. Try again later.");
                }
            } else {
                throw new BadRequestException("User is locked. Try again later.");
            }
        }

        // max attempts -> lock
        if (user.getLoginCount() >= MAX_LOGIN_ATTEMPTS) {
            user.set_locked(true);
            user.setLastLockedDateTime(LocalDateTime.now());
            repository.save(user);
            throw new BadRequestException("User reached max login attempts and is locked for " + LOCK_MINUTES + " minutes");
        }

        // password check (CORRECT WAY)
        if (!passwordEncoder.matches(signinDto.getPassword(), user.getPassword())) {
            user.setLoginCount(user.getLoginCount() + 1);

            if (user.getLoginCount() >= MAX_LOGIN_ATTEMPTS) {
                user.set_locked(true);
                user.setLastLockedDateTime(LocalDateTime.now());
            }

            repository.save(user);
            throw new BadRequestException("Incorrect password");
        }

        // success
        user.setLoginCount(0);
        user.set_locked(false);
        repository.save(user);
    }

    @Override
    @Transactional
    public ResetPasswordDto resetPassword(ResetPasswordDto resetPasswordDto) {
        if (resetPasswordDto == null) throw new BadRequestException("Reset data is required");
        if (resetPasswordDto.getEmail() == null || resetPasswordDto.getEmail().isBlank())
            throw new BadRequestException("Email is required");
        if (resetPasswordDto.getPassword() == null || resetPasswordDto.getPassword().isBlank())
            throw new BadRequestException("New password is required");

        User user = Optional.ofNullable(repository.findByEmail(resetPasswordDto.getEmail()))
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + resetPasswordDto.getEmail() + " not found"));

        // Ensure user has a valid role (migration support)
        if (user.getRole() == null) {
            Role defaultRole = roleRepository.findByTitleIgnoreCase("STUDENT");
            if (defaultRole == null) {
                defaultRole = new Role();
                defaultRole.setTitle("STUDENT");
                defaultRole = roleRepository.save(defaultRole);
            }
            user.setRole(defaultRole);
        }

        user.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
        repository.save(user);
        return resetPasswordDto;
    }

    @Override
    @Transactional
    public void signup(SignupDto signupDto) {
        if (signupDto == null) throw new BadRequestException("Signup data is required");
        if (signupDto.getEmail() == null || signupDto.getEmail().isBlank()) throw new BadRequestException("Email is required");
        if (signupDto.getPassword() == null || signupDto.getPassword().isBlank()) throw new BadRequestException("Password is required");
        if (signupDto.getRole() == null || signupDto.getRole().isBlank()) throw new BadRequestException("Role is required");

        User existing = repository.findByEmail(signupDto.getEmail());
        if (existing != null) {
            throw new DataAlreadyExistException("User with email " + signupDto.getEmail() + " already exists");
        }

        User user = converter.convert(signupDto, User.class);
        user.setPassword(passwordEncoder.encode(signupDto.getPassword()));

        // Assign ONE role only - validate it's ADMIN, ALUMNI, or STUDENT
        String roleTitle = signupDto.getRole().toUpperCase().trim();
        if (!roleTitle.equals("ADMIN") && !roleTitle.equals("ALUMNI") && !roleTitle.equals("STUDENT")) {
            throw new BadRequestException("Invalid role. Role must be ADMIN, ALUMNI, or STUDENT only.");
        }
        
        Role role = roleRepository.findByTitleIgnoreCase(roleTitle);
        if (role == null) {
            // Create the role if it doesn't exist
            role = new Role();
            role.setTitle(roleTitle);
            role = roleRepository.save(role);
        }
        
        // Set SINGLE role - ONE user = ONE role
        user.setRole(role);

        repository.save(user);
    }

    @Override
    @Transactional
    public List<UserDto> searchBy(SearchDto searchDto) {
        if (searchDto == null) throw new BadRequestException("Search data is required");

        // IMPORTANT: your repository method uses city + state separately.
        // If SearchDto has only "location", you must split it or change DTO.
        // Here: assume location is "city,state" (e.g. "Fairfield,IA")
        String city = null;
        String state = null;

        if (searchDto.getLocation() != null && !searchDto.getLocation().isBlank()) {
            String[] parts = searchDto.getLocation().split(",", 2);
            city = parts[0].trim();
            if (parts.length > 1) state = parts[1].trim();
        }

        List<User> users = repository.findUsersByFilter(
                searchDto.getGraduationYear(),
                searchDto.getCourse(),
                city,
                state,
                searchDto.getIndustry()
        );
        
        // Ensure all users have valid roles (migration support)
        users.forEach(user -> {
            if (user.getRole() == null) {
                Role defaultRole = roleRepository.findByTitleIgnoreCase("STUDENT");
                if (defaultRole == null) {
                    defaultRole = new Role();
                    defaultRole.setTitle("STUDENT");
                    defaultRole = roleRepository.save(defaultRole);
                }
                user.setRole(defaultRole);
                repository.save(user);
            }
        });
        
        return converter.convertList(users, UserDto.class);
    }

    @Override
    @Transactional
    public UserDto getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email is required");
        }
        User user = Optional.ofNullable(repository.findByEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
        
        // Ensure user has a valid role (migration support for existing data)
        if (user.getRole() == null) {
            Role defaultRole = roleRepository.findByTitleIgnoreCase("STUDENT");
            if (defaultRole == null) {
                defaultRole = new Role();
                defaultRole.setTitle("STUDENT");
                defaultRole = roleRepository.save(defaultRole);
            }
            user.setRole(defaultRole);
            repository.save(user);
        }
        
        return converter.convert(user, UserDto.class);
    }

    @Override
    @Transactional
    public UserDto updateRole(Long id, String roleTitle) {
        if (id == null) throw new BadRequestException("User id is required");
        if (roleTitle == null || roleTitle.isBlank()) {
            throw new BadRequestException("Role title is required");
        }

        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        // Validate role is one of the allowed roles
        String normalized = roleTitle.trim().toUpperCase();
        if (!normalized.equals("ADMIN") && !normalized.equals("ALUMNI") && !normalized.equals("STUDENT")) {
            throw new BadRequestException("Invalid role. Role must be ADMIN, ALUMNI, or STUDENT only.");
        }
        
        Role role = Optional.ofNullable(roleRepository.findByTitleIgnoreCase(normalized))
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setTitle(normalized);
                    return roleRepository.save(r);
                });

        // Replace role - ONE user = ONE role (never add, always replace)
        user.setRole(role);
        User saved = repository.save(user);
        return converter.convert(saved, UserDto.class);
    }

    @Override
    @Transactional
    public String adminResetPassword(Long id) {
        if (id == null) throw new BadRequestException("User id is required");

        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        // Generate a random password (you might want to use a more secure method)
        String newPassword = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(newPassword));

        repository.save(user);
        return newPassword;
    }

    private String generateRandomPassword() {
        // Simple random password generation - in production, use a proper password generator
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
