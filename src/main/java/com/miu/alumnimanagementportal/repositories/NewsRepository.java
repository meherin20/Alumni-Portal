package com.miu.alumnimanagementportal.repositories;

import com.miu.alumnimanagementportal.entities.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
}