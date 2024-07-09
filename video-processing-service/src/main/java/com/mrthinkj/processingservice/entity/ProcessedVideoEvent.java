package com.mrthinkj.processingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "processed-events")
public class ProcessedVideoEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1234567L;
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = true)
    private String idempotencyKey;
    @Column(nullable = false)
    private String videoUUID;
}
