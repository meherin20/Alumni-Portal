package com.miu.alumnimanagementportal.services;


import com.miu.alumnimanagementportal.dtos.EducationDetailsDto;
import com.miu.alumnimanagementportal.dtos.RoleDto;

import java.util.List;

public interface RoleService {
    RoleDto create(RoleDto roleDto);

    List<RoleDto> findAll();

    RoleDto update(RoleDto roleDto, Long id);

    RoleDto getRoleById(Long id);

    void delete(Long id);
}
