package com.example.demo.repositories;

import com.example.demo.entities.LocationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationDataRepository extends JpaRepository<LocationData, Long> {
}