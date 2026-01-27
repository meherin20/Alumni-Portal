package com.miu.alumnimanagementportal.repositories;

import com.miu.alumnimanagementportal.entities.Memory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoryRepository extends JpaRepository<Memory, Long> {
    
    List<Memory> findByPublishedTrueOrderByCreatedDateDesc();
    
    List<Memory> findByUserIdOrderByCreatedDateDesc(Long userId);
    
    @Query("SELECT m FROM Memory m WHERE m.published = true ORDER BY m.createdDate DESC")
    List<Memory> findAllPublishedMemories();
}
