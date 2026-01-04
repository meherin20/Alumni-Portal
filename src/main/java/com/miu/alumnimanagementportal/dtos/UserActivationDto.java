package com.miu.alumnimanagementportal.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserActivationDto {
    private  Long id;
    private boolean is_active = true;
}
