package com.miu.alumnimanagementportal.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.miu.alumnimanagementportal.common.enums.NewsType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link com.miu.alumnimanagementportal.entities.News}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsDto implements Serializable {
    private Long id;
    private Long version;
    private Date createdDate;
    private Date lastModifiedDate;
    @NotNull
    @NotEmpty
    @NotBlank
    private String title;
    @NotNull
    private NewsType newsType;
    @NotNull
    @NotEmpty
    @NotBlank
    private String description;
}