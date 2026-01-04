package com.miu.alumnimanagementportal.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link com.miu.alumnimanagementportal.entities.ProfessionalAchievement}
 */
@Data
public class ProfessionalAchievementDto implements Serializable {
    Long id;

    Long version;

    Date createdDate;

    Date lastModifiedDate;
    @NotNull
    @NotEmpty
    @NotBlank
    String title;
    @NotNull
    Date year;
    @NotNull
    @NotEmpty
    @NotBlank
    String description;
}