package com.miu.alumnimanagementportal.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
public class Attendant extends BaseEntity{
    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "boolean default false")
    private boolean confirmed;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

}
