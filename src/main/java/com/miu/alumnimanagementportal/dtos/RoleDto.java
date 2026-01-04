package com.miu.alumnimanagementportal.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link com.miu.alumnimanagementportal.entities.Role}
 */
@Data
public class RoleDto implements Serializable {
    Long id;
    Long version;
    @NotNull
    Date createdDate;
    @NotNull
    Date lastModifiedDate;
    @NotNull
    @NotEmpty
    @NotBlank
    String title;
}