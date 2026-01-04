package com.miu.alumnimanagementportal.repositories;

import com.miu.alumnimanagementportal.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}