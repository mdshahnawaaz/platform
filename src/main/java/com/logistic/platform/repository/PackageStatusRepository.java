package com.logistic.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logistic.platform.models.PackageStatus;

public interface PackageStatusRepository extends JpaRepository<PackageStatus, Long>{

    List<PackageStatus> findByTrackingIdOrderByTimestampAsc(int trackingId);
    
}
