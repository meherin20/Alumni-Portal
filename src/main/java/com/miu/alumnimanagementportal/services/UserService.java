package com.miu.alumnimanagementportal.services;

import com.miu.alumnimanagementportal.dtos.*;

import java.util.List;

public interface UserService {

    UserDto register(UserDto userDto);


    List<UserDto> findAll();

    UserDto update(UserDto userDto, Long id);

    UserDto getUserById(Long id);

    void delete(Long id);

    UserActivationDto update(UserActivationDto userActivationDto, Long id);

    void login(SigninDto signinDto);
    ResetPasswordDto resetPassword(ResetPasswordDto resetPassworddto);


    void signup(SignupDto signinDto);

    List<UserDto> searchBy(SearchDto searchDto);

    UserDto getUserByEmail(String email);

    /**
     * Update a user's primary role (e.g. ADMIN, ALUMNI, MODERATOR).
     */
    UserDto updateRole(Long id, String roleTitle);

    /**
     * Admin reset password - generates a new random password for the user
     */
    String adminResetPassword(Long id);
}
