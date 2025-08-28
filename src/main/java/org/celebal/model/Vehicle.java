package org.celebal.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "vehicles", uniqueConstraints = {
        @UniqueConstraint(name = "uk_vehicles_reg_no", columnNames = {"reg_no"}),
        @UniqueConstraint(name = "uk_vehicles_owner", columnNames = {"owner_user_id"})
})
public class Vehicle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "vehicle_id", nullable = false, updatable = false)
    private UUID vehicleId;

    @Column(name = "vehicle_name", nullable = false, length = 150)
    private String vehicleName;

    @Column(name = "reg_no", nullable = false, length = 50)
    private String regNo;

    @OneToOne(optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false, unique = true)
    private User owner;

    @OneToOne(mappedBy = "vehicle")
    private Device device;
}


