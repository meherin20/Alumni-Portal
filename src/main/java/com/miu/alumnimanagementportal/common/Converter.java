package com.miu.alumnimanagementportal.common;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Converter {
    private final ModelMapper modelMapper;

    public <T,R> List<R> convertList(List<T> list, Class<R> destinationType) {
        return list.stream().map(item -> modelMapper.map(item, destinationType)).collect(Collectors.toList());
    }

    public <T,R> R convert(T source, Class<R> destinationType) {
        return modelMapper.map(source, destinationType);
    }

    public <T> ResponseEntity<T> buildResponseEntity(T message, HttpStatus status) {
        return new ResponseEntity<>(message, status);
    }
}
