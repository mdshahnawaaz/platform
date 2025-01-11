package com.logistic.platform.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.logistic.platform.models.PackageStatus;
import com.logistic.platform.models.Package;
import com.logistic.platform.repository.PackageRepository;
import com.logistic.platform.repository.PackageStatusRepository;

@Service
public class PackageService {

    private final PackageRepository packageRepository;
    private final PackageStatusRepository packageStatusRepository;

    public PackageService(PackageRepository packageRepository, PackageStatusRepository packageStatusRepository) {
        this.packageRepository = packageRepository;
        this.packageStatusRepository = packageStatusRepository;
    }

    public Package getPackageByTrackingId(int trackingId) {
        return packageRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Package not found with Tracking ID: " + trackingId));
    }

    public List<PackageStatus> getPackageStatusHistory(int trackingId) {
        return packageStatusRepository.findByTrackingIdOrderByTimestampAsc(trackingId);
    }

    public void updatePackageStatus(int trackingId, String status, String location) {
        Package pkg = getPackageByTrackingId(trackingId);
        pkg.setCurrentStatus(status);
        pkg.setLastUpdated(LocalDateTime.now());
        packageRepository.save(pkg);

        // Add status to history
        PackageStatus packageStatus = new PackageStatus();
        packageStatus.setTrackingId(trackingId);
        packageStatus.setStatus(status);
        packageStatus.setLocation(location);
        packageStatus.setTimestamp(LocalDateTime.now());
        packageStatusRepository.save(packageStatus);
    }

}
