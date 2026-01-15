package com.miu.alumnimanagementportal.services;

import com.miu.alumnimanagementportal.dtos.AlumniDirectoryDto;
import com.miu.alumnimanagementportal.dtos.ConnectionStatusDto;

import java.util.List;

public interface AlumniDirectoryService {
    List<AlumniDirectoryDto> searchAlumni(String name, String email, String department, String graduationYear, String jobTitle, String company);
    AlumniDirectoryDto getAlumniProfile(Long id);
    ConnectionStatusDto checkConnectionStatus(String studentEmail, String alumniEmail);
}

