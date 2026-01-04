package com.miu.alumnimanagementportal.entities.common;

public interface NonDeletable {
    Boolean isDeleted();

    void setDeleted(Boolean deleted);

    default boolean isNotDeleted() {

        return isDeleted() == null || !isDeleted();
    }
}