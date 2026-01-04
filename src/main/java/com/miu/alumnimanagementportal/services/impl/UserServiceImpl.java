package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.Converter;
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
import java.util.Set;

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
    public UserDto register(UserDto userDto) {
        if (userDto == null) throw new BadRequestException("User data is required");

        // if id is provided and already exists -> conflict
        if (userDto.getId() != null && repository.existsById(userDto.getId())) {
            throw new DataAlreadyExistException("User with id " + userDto.getId() + " already exists");
        }

        User user = converter.convert(userDto, User.class);

        // encode password if present
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        User saved = repository.save(user);
        return converter.convert(saved, UserDto.class);
    }

    @Override
    public List<UserDto> findAll() {
        return converter.convertList(repository.findAll(), UserDto.class);
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Long id) {
        if (id == null) throw new BadRequestException("User id is required");
        if (userDto == null) throw new BadRequestException("User data is required");

        User existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        User toSave = converter.convert(userDto, User.class);
        toSave.setId(existing.getId()); // ensure we update the same row

        // only encode if password provided (otherwise keep existing)
        if (toSave.getPassword() == null || toSave.getPassword().isBlank()) {
            toSave.setPassword(existing.getPassword());
        } else {
            toSave.setPassword(passwordEncoder.encode(toSave.getPassword()));
        }

        User saved = repository.save(toSave);
        return converter.convert(saved, UserDto.class);
    }

    @Override
    public UserDto getUserById(Long id) {
        if (id == null) throw new BadRequestException("User id is required");

        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

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

        User user = Optional.ofNullable(repository.findByEmail(signinDto.getEmail()))
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + signinDto.getEmail() + " not found"));

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

        // Assign the appropriate role
        Role role = roleRepository.findByTitleIgnoreCase(signupDto.getRole().toUpperCase());
        if (role == null) {
            // Create the role if it doesn't exist
            role = new Role();
            role.setTitle(signupDto.getRole().toUpperCase());
            role = roleRepository.save(role);
        }
        user.setRoles(Set.of(role));

        repository.save(user);
    }

    @Override
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

        return converter.convertList(
                repository.findUsersByFilter(
                        searchDto.getGraduationYear(),
                        searchDto.getCourse(),
                        city,
                        state,
                        searchDto.getIndustry()
                ),
                UserDto.class
        );
    }

    @Override
    public UserDto getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email is required");
        }
        User user = Optional.ofNullable(repository.findByEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
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

        String normalized = roleTitle.trim().toUpperCase();
        Role role = Optional.ofNullable(roleRepository.findByTitleIgnoreCase(normalized))
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setTitle(normalized);
                    return roleRepository.save(r);
                });

        user.setRoles(Set.of(role));
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
