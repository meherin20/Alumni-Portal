package com.miu.alumnimanagementportal.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass
public class BaseEntity implements Comparable<BaseEntity>, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @Column(name = "last_modified_date", nullable = false)
    private Date lastModifiedDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = new Date();
    }

    @Override
    public int compareTo(BaseEntity object) {

        return this.getId().compareTo(object.getId());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BaseEntity{");
        sb.append("version=").append(version);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", lastModifiedDate=").append(lastModifiedDate);
        sb.append('}');
        return sb.toString();
    }
}
