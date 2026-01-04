package com.miu.alumnimanagementportal.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link com.miu.alumnimanagementportal.entities.Address}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDto implements Serializable {
    private Long id;
    private Long version;
    private Date createdDate;
    private Date lastModifiedDate;
    @NotNull
    @NotEmpty
    @NotBlank
    private String city;
    @NotNull
    @NotEmpty
    @NotBlank
    private String state;
    @NotNull
    @NotEmpty
    @NotBlank
    private String country;
    @NotNull
    @NotEmpty
    @NotBlank
    private String zip;
    private String street;
}