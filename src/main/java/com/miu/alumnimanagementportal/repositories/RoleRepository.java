package com.miu.alumnimanagementportal.repositories;

import com.miu.alumnimanagementportal.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByTitleIgnoreCase(String title);
}