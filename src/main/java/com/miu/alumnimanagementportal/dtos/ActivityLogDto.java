package com.miu.alumnimanagementportal.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.miu.alumnimanagementportal.entities.common.ActivityLog}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityLogDto implements Serializable {
    private Long id;
    @NotNull
    private LocalDateTime accessTime;
    @NotNull
    @NotEmpty
    @NotBlank
    private String operation;
    private UserDto user;
    @NotNull
    @NotEmpty
    @NotBlank
    private String ipAddress;
}