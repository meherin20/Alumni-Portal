package com.miu.alumnimanagementportal.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "memories")
@Data
public class Memory extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;
    
    @Column(name = "story", nullable = false, columnDefinition = "TEXT")
    private String story;
    
    @Column(name = "published", nullable = false)
    private Boolean published = true;
}
