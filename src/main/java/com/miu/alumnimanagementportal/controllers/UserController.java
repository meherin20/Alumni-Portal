package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.*;
import com.miu.alumnimanagementportal.services.ProfileService;
import com.miu.alumnimanagementportal.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final Converter converter;
    private final ProfileService profileService;


    @PostMapping("/activate/{id}")
    public ResponseEntity<?> activate(@PathVariable Long id) {
        userService.update(new UserActivationDto(), id);
        return converter.buildResponseEntity(Map.of("message", "User activated successfully"), HttpStatus.ACCEPTED);
    }

    @PostMapping("/deactivate/{id}")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        userService.update(new UserActivationDto(id, false), id);
        return converter.buildResponseEntity(Map.of("message", "User deactivated successfully"), HttpStatus.ACCEPTED);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto) {
        userService.register(userDto);
        return converter.buildResponseEntity(Map.of("message", "User registered successfully"), HttpStatus.ACCEPTED);
    }


    @GetMapping
    public ResponseEntity<?> getUserAll() {
        return converter.buildResponseEntity(Map.of("data", userService.findAll()), HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return converter.buildResponseEntity(Map.of("data", userService.getUserById(id)), HttpStatus.OK);
    }

    @GetMapping("/by-email")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        return converter.buildResponseEntity(Map.of("data", userService.getUserByEmail(email)), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserById( @PathVariable Long id,@Valid @RequestBody UserDto userDto) {
        return converter.buildResponseEntity(Map.of("data", userService.update(userDto, id)), HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        userService.delete(id);
        return converter.buildResponseEntity(Map.of("message", "User Deleted successfully"), HttpStatus.ACCEPTED);
    }

    @PostMapping("/{id}/profile")
    public ResponseEntity<?> createProfile(@Valid @RequestBody ProfileDto profileDto) {
        profileService.create(profileDto);
        return converter.buildResponseEntity(Map.of("message", "Profile created successfully"), HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getProfileById(@PathVariable Long id) {
        return converter.buildResponseEntity(Map.of("data", profileService.getProfileById(id)), HttpStatus.OK);
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateProfileById( @PathVariable Long id,@Valid @RequestBody ProfileDto profileDto) {
        return converter.buildResponseEntity(Map.of("data", profileService.update(profileDto, id)), HttpStatus.OK);
    }


    @DeleteMapping("/{id}/profile")
    public ResponseEntity<?> deleteProfileById(@PathVariable Long id) {
        profileService.delete(id);
        return converter.buildResponseEntity(Map.of("message", "Profile Deleted successfully"), HttpStatus.ACCEPTED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody SigninDto signinDto) {
        userService.login(signinDto);
        return converter.buildResponseEntity(Map.of("message", "User logged in successfully"), HttpStatus.ACCEPTED);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupDto signupDto) {
        userService.signup(signupDto);
        return converter.buildResponseEntity(Map.of("message", "User registered successfully as " + signupDto.getRole().toUpperCase()), HttpStatus.CREATED);
    }

    @GetMapping("/searchBy")
    public ResponseEntity<?> searchBy(@Valid @RequestBody SearchDto searchDto) {
        return converter.buildResponseEntity(Map.of("data", userService.searchBy(searchDto)), HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@Valid @RequestBody SearchDto searchDto) {
        return converter.buildResponseEntity(Map.of("data", userService.searchBy(searchDto)), HttpStatus.OK);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        return converter.buildResponseEntity(Map.of("data", userService.resetPassword(resetPasswordDto)), HttpStatus.OK);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestParam String title) {
        return converter.buildResponseEntity(
                Map.of("data", userService.updateRole(id, title)), HttpStatus.OK);
    }

}
