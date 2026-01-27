package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.MemoryDto;
import com.miu.alumnimanagementportal.entities.Memory;
import com.miu.alumnimanagementportal.entities.User;
import com.miu.alumnimanagementportal.exceptions.BadRequestException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.MemoryRepository;
import com.miu.alumnimanagementportal.repositories.UserRepository;
import com.miu.alumnimanagementportal.services.MemoryService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MemoryServiceImpl implements MemoryService {
    
    private final MemoryRepository memoryRepository;
    private final UserRepository userRepository;
    private final Converter converter;
    
    private static final String UPLOAD_DIR = "uploads/memories/";
    
    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }
    
    @Override
    @Transactional
    public MemoryDto create(MemoryDto memoryDto, MultipartFile image, String userEmail) {
        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Image is required");
        }
        
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        
        Memory memory = new Memory();
        memory.setUser(user);
        memory.setStory(memoryDto.getStory());
        memory.setPublished(memoryDto.getPublished() != null ? memoryDto.getPublished() : true);
        
        // Handle image upload
        String imageFileName = saveFile(image);
        memory.setImageUrl(imageFileName);
        
        Memory savedMemory = memoryRepository.save(memory);
        return convertToDto(savedMemory);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MemoryDto> findAll() {
        return memoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MemoryDto> findAllPublished() {
        return memoryRepository.findAllPublishedMemories().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MemoryDto> findByUserEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return memoryRepository.findByUserIdOrderByCreatedDateDesc(user.getId()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public MemoryDto getMemoryById(Long id) {
        Memory memory = memoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Memory with id " + id + " not found"));
        return convertToDto(memory);
    }
    
    @Override
    @Transactional
    public void delete(Long id, String userEmail) {
        Memory memory = memoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Memory with id " + id + " not found"));
        
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        
        // Only allow deletion if user is the owner or admin
        if (!memory.getUser().getId().equals(user.getId()) && 
            (user.getRole() == null || !"ADMIN".equalsIgnoreCase(user.getRole().getTitle()))) {
            throw new BadRequestException("You don't have permission to delete this memory");
        }
        
        memoryRepository.delete(memory);
    }
    
    private String saveFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String filename = "memory_" + UUID.randomUUID().toString() + extension;
            
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image file", e);
        }
    }
    
    private MemoryDto convertToDto(Memory memory) {
        // Manually create DTO to avoid ModelMapper ambiguity issues
        MemoryDto dto = new MemoryDto();
        dto.setId(memory.getId());
        dto.setVersion(memory.getVersion());
        dto.setCreatedDate(memory.getCreatedDate());
        dto.setLastModifiedDate(memory.getLastModifiedDate());
        dto.setStory(memory.getStory());
        dto.setImageUrl(memory.getImageUrl());
        dto.setPublished(memory.getPublished());
        
        if (memory.getUser() != null) {
            dto.setUserId(memory.getUser().getId());
            dto.setUserName(memory.getUser().getFirstName() + " " + memory.getUser().getLastName());
            dto.setUserEmail(memory.getUser().getEmail());
        }
        
        return dto;
    }
}
