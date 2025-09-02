package org.celebal.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vehicle_smartcar_mapping")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleSmartcarMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "vehicle_id", nullable = false, unique = true)
    private UUID vehicleId;

    @Column(name = "smartcar_vehicle_id", nullable = false, unique = true, length = 100)
    private String smartcarVehicleId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "vehicle_info", columnDefinition = "TEXT")
    private String vehicleInfo; // Store make, model, year from Smartcar as JSON string

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "last_location_fetch")
    private Instant lastLocationFetch;

    @Column(name = "last_location_latitude")
    private Double lastLocationLatitude;

    @Column(name = "last_location_longitude")
    private Double lastLocationLongitude;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ===== RELATIONSHIPS =====

    // Note: We don't use the Vehicle entity relationship since we store everything in this mapping table
    // The vehicle_id is just a unique identifier, not a foreign key to the vehicles table
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    // ===== BUSINESS LOGIC METHODS =====

    /**
     * Check if the mapping is active and can be used for location fetching
     */
    public boolean canFetchLocation() {
        return isActive && smartcarVehicleId != null && !smartcarVehicleId.trim().isEmpty();
    }

    /**
     * Update last location information
     */
    public void updateLastLocation(Double latitude, Double longitude) {
        this.lastLocationLatitude = latitude;
        this.lastLocationLongitude = longitude;
        this.lastLocationFetch = Instant.now();
    }

    /**
     * Get vehicle info as formatted string
     */
    public String getFormattedVehicleInfo() {
        if (vehicleInfo == null || vehicleInfo.trim().isEmpty()) {
            return "Unknown Vehicle";
        }
        // TODO: Parse JSON and format nicely
        return vehicleInfo;
    }

    /**
     * Check if location was fetched recently (within specified minutes)
     */
    public boolean wasLocationFetchedRecently(int minutes) {
        if (lastLocationFetch == null) {
            return false;
        }
        Instant threshold = Instant.now().minusSeconds(minutes * 60);
        return lastLocationFetch.isAfter(threshold);
    }

    // ===== BUILDER METHODS =====

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (updatedAt == null) {
            updatedAt = Instant.now();
        }
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
