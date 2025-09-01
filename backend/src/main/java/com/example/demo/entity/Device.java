package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "devices")
public class Device {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String deviceId;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}