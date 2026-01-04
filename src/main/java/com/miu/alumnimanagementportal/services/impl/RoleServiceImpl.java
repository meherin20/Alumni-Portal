package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.ProfileDto;
import com.miu.alumnimanagementportal.dtos.RoleDto;
import com.miu.alumnimanagementportal.entities.Profile;
import com.miu.alumnimanagementportal.entities.Role;
import com.miu.alumnimanagementportal.exceptions.DataAlreadyExistException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.RoleRepository;
import com.miu.alumnimanagementportal.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository repository;
    private final Converter converter;
    @Override
    public RoleDto create(RoleDto roleDto) {
        Optional.ofNullable(roleDto.getId()).ifPresent(id -> {
            if (repository.existsById(id)) {
                throw new DataAlreadyExistException("Role with id " + id + " already exists");
            }
        });
        return converter.convert(repository.save(converter.convert(roleDto, Role.class)), RoleDto.class);
    }

    @Override
    public List<RoleDto> findAll() {
        return converter.convertList(repository.findAll(), RoleDto.class);
    }

    @Override
    public RoleDto update(RoleDto roleDto, Long id) {
        return Optional.ofNullable(roleDto.getId()).map(entityId -> {
            if (!repository.existsById(entityId)) {
                throw new ResourceNotFoundException("Role with id " + entityId + " not found");
            }
            return converter.convert(
                    repository.save(
                            converter.convert(roleDto, Role.class)
                    ), RoleDto.class);
        }).orElseThrow(() -> new ResourceNotFoundException("Role with id " + id + " not found"));
    }

    @Override
    public RoleDto getRoleById(Long id) {
        return Optional.ofNullable(id)
                .map(repository::findById)
                .map(role -> converter.convert(role, RoleDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Role information with id " + id + " not found"));
    }

    @Override
    public void delete(Long id) {
        Optional.ofNullable(id).ifPresent(repository::deleteById);
    }
}
