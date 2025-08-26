package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID groupId;

    private String groupName;

    private UUID adminId;

    private Long rideStart;

    private Long rideEnd;
}