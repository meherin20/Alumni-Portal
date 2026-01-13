package com.miu.alumnimanagementportal.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;

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
     * Basic validation: minimum 3 characters, accepts letters and numbers.
     */
    @Size(message = "Password must be at least 3 characters", min = 3)
    String password;

    // ONE user = ONE role only (ADMIN, ALUMNI, or STUDENT)
    RoleDto role;

    ProfileDto profile;
}