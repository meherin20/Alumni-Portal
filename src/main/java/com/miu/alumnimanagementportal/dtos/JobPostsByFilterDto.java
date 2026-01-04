package com.miu.alumnimanagementportal.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobPostsByFilterDto {

    @NotNull
    @NotEmpty
    @NotBlank
    private String location;

    @NotNull
    @NotEmpty
    @NotBlank
    private String companyName;

    @NotNull
    @NotEmpty
    @NotBlank
    private String city;

    @NotNull
    @NotEmpty
    @NotBlank
    private String state;
}
