package com.miu.alumnimanagementportal.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupDto {
    @NotNull
    @NotEmpty
    @NotBlank
    private String firstName;

    @NotNull
    @NotEmpty
    @NotBlank
    private String lastName;

    @NotNull
    @NotEmpty
    @NotBlank
    private String email;

    @NotNull
    @NotEmpty
    @NotBlank
    private String password;

    @NotNull
    @NotEmpty
    @NotBlank
    private String role; // STUDENT, ALUMNI, ADMIN
}
