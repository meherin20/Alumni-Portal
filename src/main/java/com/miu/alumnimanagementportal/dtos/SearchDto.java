package com.miu.alumnimanagementportal.dtos;

import lombok.Data;

@Data
public class SearchDto {
    private String graduationYear;
    private String course;
    private String location;
    private String industry;
}
