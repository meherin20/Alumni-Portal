package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.JobPostDto;
import com.miu.alumnimanagementportal.dtos.NewsDto;
import com.miu.alumnimanagementportal.entities.JobPost;
import com.miu.alumnimanagementportal.entities.News;
import com.miu.alumnimanagementportal.exceptions.DataAlreadyExistException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.JobPostRepository;
import com.miu.alumnimanagementportal.repositories.NewsRepository;
import com.miu.alumnimanagementportal.services.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NewsServiceImpl implements NewsService {
    private final NewsRepository newsRepository;
    private final Converter converter;

    private static final String UPLOAD_DIR = "uploads/news/";

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
    public NewsDto create(NewsDto newsDto, MultipartFile photo) {
        Optional.ofNullable(newsDto.getId()).ifPresent(id -> {
            if (newsRepository.existsById(id)) {
                throw new DataAlreadyExistException("News with id " + id + " already exists");
            }
        });

        News news = converter.convert(newsDto, News.class);

        // Handle photo upload
        if (photo != null && !photo.isEmpty()) {
            String photoFileName = saveFile(photo);
            news.setPhotoUrl(photoFileName);
        }

        News savedNews = newsRepository.save(news);
        return converter.convert(savedNews, NewsDto.class);
    }

    @Override
    public List<NewsDto> findAll() {
        return newsRepository.findAll().stream()
                .map(element -> converter.convert(element, NewsDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public NewsDto update(NewsDto newsDto, Long id, MultipartFile photo) {
        News existingNews = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News with id " + id + " not found"));

        // Update fields
        existingNews.setTitle(newsDto.getTitle());
        existingNews.setNewsType(newsDto.getNewsType());
        existingNews.setDescription(newsDto.getDescription());

        // Handle photo upload
        if (photo != null && !photo.isEmpty()) {
            String photoFileName = saveFile(photo);
            existingNews.setPhotoUrl(photoFileName);
        }

        News savedNews = newsRepository.save(existingNews);
        return converter.convert(savedNews, NewsDto.class);
    }

    @Override
    public NewsDto getNewsById(Long id) {
        return Optional.ofNullable(id)
                .map(newsRepository::findById)
                .map(element -> converter.convert(element, NewsDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("News with id " + id + " not found"));
    }

    @Override
    public void delete(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new ResourceNotFoundException("News with id " + id + " not found");
        }
        newsRepository.deleteById(id);
    }

    private String saveFile(MultipartFile file) {
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created upload directory: " + uploadPath.toAbsolutePath());
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String filename = "news_" + UUID.randomUUID().toString() + extension;

            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            System.out.println("Saved file to: " + filePath.toAbsolutePath());
            System.out.println("File URL will be: /uploads/news/" + filename);

            return filename;
        } catch (IOException e) {
            System.err.println("Failed to save photo file: " + e.getMessage());
            throw new RuntimeException("Failed to save photo file", e);
        }
    }
}
