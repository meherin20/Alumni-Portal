package com.miu.alumnimanagementportal.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SigninDto {

    private String email;
    private String password;
    private String role; // STUDENT, ALUMNI, ADMIN

}
