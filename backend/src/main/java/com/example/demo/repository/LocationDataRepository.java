package com.example.demo.repository;

import com.example.demo.model.LocationData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationDataRepository extends JpaRepository<LocationData, Long> {
    // Custom query methods can be defined here
}