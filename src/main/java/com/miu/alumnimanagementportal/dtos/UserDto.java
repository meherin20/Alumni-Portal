package com.miu.alumnimanagementportal.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * DTO for {@link com.miu.alumnimanagementportal.entities.User}
 */
@Data
public class UserDto implements Serializable {
    Long id;
    Long version;
    Date createdDate;
    Date lastModifiedDate;
    String firstName;
    String lastName;
    @Email
    String email;
    /**
     * Password is optional here so that profile-only updates (e.g. from the
     * alumni portal) can succeed without forcing the client to send it.
     * Length validation is only applied when a non-null value is provided.
     */
    @Size(message = "Password length should be between 5 and 10", min = 5, max = 10)
    String password;

    Set<RoleDto> roles;

    ProfileDto profile;
}