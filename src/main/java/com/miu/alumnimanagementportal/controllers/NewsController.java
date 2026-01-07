package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.JobPostDto;
import com.miu.alumnimanagementportal.dtos.NewsDto;
import com.miu.alumnimanagementportal.services.JobPostService;
import com.miu.alumnimanagementportal.services.NewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/news")
public class NewsController {
    private final NewsService newsService;
    private final Converter converter;

    @PostMapping
    public ResponseEntity<?> createNews(
            @Valid @ModelAttribute NewsDto newsDto,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        try {
            NewsDto createdNews = newsService.create(newsDto, photo);
            return converter.buildResponseEntity(
                    Map.of("message", "News created successfully", "data", createdNews),
                    HttpStatus.CREATED);
        } catch (Exception e) {
            return converter.buildResponseEntity(
                    Map.of("message", "Failed to create news: " + e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> getNewsAll() {
        return converter.buildResponseEntity(Map.of("data", newsService.findAll()), HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNewsById(@PathVariable Long id) {
        return converter.buildResponseEntity(Map.of("data", newsService.getNewsById(id)), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNewsById(
            @PathVariable Long id,
            @Valid @ModelAttribute NewsDto newsDto,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        try {
            NewsDto updatedNews = newsService.update(newsDto, id, photo);
            return converter.buildResponseEntity(
                    Map.of("message", "News updated successfully", "data", updatedNews),
                    HttpStatus.OK);
        } catch (Exception e) {
            return converter.buildResponseEntity(
                    Map.of("message", "Failed to update news: " + e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNewsById(@PathVariable Long id) {
        newsService.delete(id);
        return converter.buildResponseEntity(Map.of("message", "News Deleted successfully"), HttpStatus.ACCEPTED);
    }

    @GetMapping("/photo/{filename:.+}")
    public ResponseEntity<byte[]> getNewsPhoto(@PathVariable String filename) {
        try {
            // Extract just the filename if path is included (e.g., "/uploads/news/filename.png" -> "filename.png")
            String actualFilename = filename;
            if (filename.contains("/")) {
                actualFilename = filename.substring(filename.lastIndexOf("/") + 1);
            }
            // Remove any leading slashes
            actualFilename = actualFilename.replaceAll("^/+", "");
            
            System.out.println("Requested filename: " + filename);
            System.out.println("Extracted filename: " + actualFilename);
            
            // Try relative path first (for development)
            java.nio.file.Path filePath = java.nio.file.Paths.get("uploads/news/", actualFilename);
            
            // If not found, try absolute path
            if (!java.nio.file.Files.exists(filePath)) {
                String absolutePath = System.getProperty("user.dir") + "/uploads/news/" + actualFilename;
                filePath = java.nio.file.Paths.get(absolutePath);
            }
            
            if (!java.nio.file.Files.exists(filePath)) {
                System.err.println("Photo not found: " + actualFilename + " (original: " + filename + ")");
                return ResponseEntity.notFound().build();
            }
            
            System.out.println("Serving photo from: " + filePath.toAbsolutePath());

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
            System.err.println("Error serving photo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
