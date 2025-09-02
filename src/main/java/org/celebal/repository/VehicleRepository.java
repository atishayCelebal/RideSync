package org.celebal.repository;

import org.celebal.model.User;
import org.celebal.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    Optional<Vehicle> findByOwner(User owner);
    Optional<Vehicle> findByRegNo(String regNo);
    boolean existsByOwner(User owner);
    boolean existsByRegNo(String regNo);
    
    // Find vehicles by group through user membership
    @Query("SELECT v FROM Vehicle v JOIN GroupMembership gm ON v.owner = gm.user WHERE gm.group.groupId = :groupId AND gm.status = 'ACTIVE'")
    List<Vehicle> findVehiclesByGroupId(@Param("groupId") UUID groupId);
}


