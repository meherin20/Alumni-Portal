package com.miu.alumnimanagementportal.services;


import com.miu.alumnimanagementportal.dtos.NewsDto;
import com.miu.alumnimanagementportal.dtos.NewsDto;

import java.util.List;

public interface NewsService {
    void create(NewsDto newsDto);

    List<NewsDto> findAll();

    NewsDto update(NewsDto newsDto, Long id);

    NewsDto getNewsById(Long id);

    void delete(Long id);
}
