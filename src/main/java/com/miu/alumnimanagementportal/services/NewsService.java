package com.miu.alumnimanagementportal.services;

import com.miu.alumnimanagementportal.dtos.NewsDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NewsService {
    NewsDto create(NewsDto newsDto, MultipartFile photo);

    List<NewsDto> findAll();

    NewsDto update(NewsDto newsDto, Long id, MultipartFile photo);

    NewsDto getNewsById(Long id);

    void delete(Long id);
}
