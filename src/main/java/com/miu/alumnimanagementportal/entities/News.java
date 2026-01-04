package com.miu.alumnimanagementportal.entities;

import com.miu.alumnimanagementportal.common.enums.NewsType;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class News extends BaseEntity{
    private String title;
    private NewsType newsType;
    private String description;

}
