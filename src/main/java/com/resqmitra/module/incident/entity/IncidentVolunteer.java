package com.resqmitra.module.incident.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.resqmitra.module.user.entity.User;
import com.resqmitra.module.user.entity.UserIdSerializer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "incident_volunteers" , uniqueConstraints = {
        @UniqueConstraint(columnNames = {"incident_id", "volunteer_id"})
    })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentVolunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "incident_id")
    @JsonSerialize(using = IncidentIdSerializer.class)
    private Incident incident;

    @ManyToOne
    @JoinColumn(name = "volunteer_id")
    @JsonSerialize(using = UserIdSerializer.class)
    private User volunteer;

    @Builder.Default
    private LocalDateTime joinedAt = LocalDateTime.now(ZoneId.systemDefault());

}

