package com.logistic.platform.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logistic.platform.models.DemandFeatureSnapshot;

public interface DemandFeatureSnapshotRepository extends JpaRepository<DemandFeatureSnapshot, Long> {

    List<DemandFeatureSnapshot> findTop50ByOrderBySnapshotTimeDesc();

    List<DemandFeatureSnapshot> findByLabelReadyFalseAndSnapshotTimeBefore(LocalDateTime cutoff);
}
