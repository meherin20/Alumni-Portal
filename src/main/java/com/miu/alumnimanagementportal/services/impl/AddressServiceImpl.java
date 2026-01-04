package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.AddressDto;
import com.miu.alumnimanagementportal.entities.Address;
import com.miu.alumnimanagementportal.exceptions.DataAlreadyExistException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.AddressRepository;
import com.miu.alumnimanagementportal.services.AddressService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final Converter converter;


    @Override
    public void create(AddressDto address) {
        Optional.ofNullable(address.getId()).ifPresent(id -> {
            if (addressRepository.existsById(id)) {
                throw new DataAlreadyExistException("Address with id " + id + " already exists");
            }
        });
        addressRepository.save(converter.convert(address, Address.class));
    }

    @Override
    public List<AddressDto> findAll() {
        return this.addressRepository.findAll()
                .stream()
                .map((element) -> converter.convert(element, AddressDto.class))
                .collect(Collectors.toList());

    }

    @Override
    public AddressDto update(AddressDto address, Long id) {
        return Optional.ofNullable(address.getId()).map(entityId -> {
            if (!addressRepository.existsById(entityId)) {
                throw new ResourceNotFoundException("Address with id " + entityId + " not found");
            }
            return converter.convert(addressRepository.save(converter.convert(address, Address.class)), AddressDto.class);
        }).orElseThrow(() -> new ResourceNotFoundException("Address with id " + id + " not found"));
    }

    @Override
    public AddressDto getAddressById(Long id) {
        return Optional.ofNullable(id)
                .map(addressRepository::findById)
                .map(element -> converter.convert(element, AddressDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Address with id " + id + " not found"));
    }

    @Override
    public void delete(Long address) {
        if (!addressRepository.existsById(address)) {
            throw new ResourceNotFoundException("Address with id " + address + " not found");
        }
        addressRepository.deleteById(address);
    }
}
