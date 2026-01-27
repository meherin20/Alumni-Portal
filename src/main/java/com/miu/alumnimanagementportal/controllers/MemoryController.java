package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.MemoryDto;
import com.miu.alumnimanagementportal.services.MemoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/memories")
public class MemoryController {
    
    private final MemoryService memoryService;
    private final Converter converter;
    
    @PostMapping
    public ResponseEntity<?> createMemory(
            @Valid @ModelAttribute MemoryDto memoryDto,
            @RequestParam("image") MultipartFile image,
            @RequestParam("userEmail") String userEmail) {
        try {
            MemoryDto createdMemory = memoryService.create(memoryDto, image, userEmail);
            return converter.buildResponseEntity(
                    Map.of("message", "Memory posted successfully", "data", createdMemory),
                    HttpStatus.CREATED);
        } catch (Exception e) {
            return converter.buildResponseEntity(
                    Map.of("message", "Failed to post memory: " + e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllMemories(@RequestParam(required = false) String published) {
        try {
            List<MemoryDto> memories;
            if ("true".equalsIgnoreCase(published)) {
                memories = memoryService.findAllPublished();
            } else {
                memories = memoryService.findAll();
            }
            return converter.buildResponseEntity(Map.of("data", memories), HttpStatus.OK);
        } catch (Exception e) {
            return converter.buildResponseEntity(
                    Map.of("message", "Failed to fetch memories: " + e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/user/{userEmail}")
    public ResponseEntity<?> getUserMemories(@PathVariable String userEmail) {
        try {
            List<MemoryDto> memories = memoryService.findByUserEmail(userEmail);
            return converter.buildResponseEntity(Map.of("data", memories), HttpStatus.OK);
        } catch (Exception e) {
            return converter.buildResponseEntity(
                    Map.of("message", "Failed to fetch user memories: " + e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getMemoryById(@PathVariable Long id) {
        try {
            MemoryDto memory = memoryService.getMemoryById(id);
            return converter.buildResponseEntity(Map.of("data", memory), HttpStatus.OK);
        } catch (Exception e) {
            return converter.buildResponseEntity(
                    Map.of("message", "Failed to fetch memory: " + e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMemory(@PathVariable Long id, @RequestParam String userEmail) {
        try {
            memoryService.delete(id, userEmail);
            return converter.buildResponseEntity(
                    Map.of("message", "Memory deleted successfully"),
                    HttpStatus.OK);
        } catch (Exception e) {
            return converter.buildResponseEntity(
                    Map.of("message", "Failed to delete memory: " + e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<byte[]> getMemoryImage(@PathVariable String filename) {
        try {
            // Extract just the filename if path is included
            String actualFilename = filename;
            if (filename.contains("/")) {
                actualFilename = filename.substring(filename.lastIndexOf("/") + 1);
            }
            // Remove any leading slashes
            actualFilename = actualFilename.replaceAll("^/+", "");
            
            System.out.println("Requested memory image filename: " + filename);
            System.out.println("Extracted filename: " + actualFilename);
            
            // Try relative path first (for development)
            java.nio.file.Path filePath = java.nio.file.Paths.get("uploads/memories/", actualFilename);
            
            // If not found, try absolute path
            if (!java.nio.file.Files.exists(filePath)) {
                String absolutePath = System.getProperty("user.dir") + "/uploads/memories/" + actualFilename;
                filePath = java.nio.file.Paths.get(absolutePath);
            }
            
            if (!java.nio.file.Files.exists(filePath)) {
                System.err.println("Memory image not found: " + actualFilename + " (original: " + filename + ")");
                return ResponseEntity.notFound().build();
            }
            
            System.out.println("Serving memory image from: " + filePath.toAbsolutePath());
            
            byte[] imageBytes = java.nio.file.Files.readAllBytes(filePath);
            String contentType = java.nio.file.Files.probeContentType(filePath);
            if (contentType == null) {
                // Try to determine content type from extension
                String lowerFilename = filename.toLowerCase();
                if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (lowerFilename.endsWith(".png")) {
                    contentType = "image/png";
                } else if (lowerFilename.endsWith(".gif")) {
                    contentType = "image/gif";
                } else {
                    contentType = "image/jpeg"; // Default
                }
            }
            
            return ResponseEntity.ok()
                    .header("Cache-Control", "max-age=3600")
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    .body(imageBytes);
        } catch (Exception e) {
            System.err.println("Error serving memory image: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
