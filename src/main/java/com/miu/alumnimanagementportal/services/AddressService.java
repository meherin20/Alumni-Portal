package com.miu.alumnimanagementportal.services;


import com.miu.alumnimanagementportal.dtos.AddressDto;
import com.miu.alumnimanagementportal.entities.Address;

import java.util.List;

public interface AddressService {
    void create(AddressDto address);

    List<AddressDto> findAll();

    AddressDto update(AddressDto address, Long id);

    AddressDto getAddressById(Long id);

    void delete(Long address);
}
