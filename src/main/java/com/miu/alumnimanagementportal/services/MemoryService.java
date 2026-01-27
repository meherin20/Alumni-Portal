package com.miu.alumnimanagementportal.services;

import com.miu.alumnimanagementportal.dtos.MemoryDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MemoryService {
    MemoryDto create(MemoryDto memoryDto, MultipartFile image, String userEmail);
    
    List<MemoryDto> findAll();
    
    List<MemoryDto> findAllPublished();
    
    List<MemoryDto> findByUserEmail(String userEmail);
    
    MemoryDto getMemoryById(Long id);
    
    void delete(Long id, String userEmail);
}
