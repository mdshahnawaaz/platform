package com.logistic.platform.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepository extends JpaRepository<Package,Long> {
    
    Optional<Package> findByTrackingId(int trackingId);

}
