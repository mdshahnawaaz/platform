package com.logistic.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.logistic.platform.models.Driver;

import io.lettuce.core.dynamic.annotation.Param;
@Repository
public interface DriverRepository extends JpaRepository<Driver,Integer>  {

    @Query(value = "select count(*) from Drivers where available=true",nativeQuery=true)
    int countFreeDrivers();

    List<Driver> findByStatus(String status); 

    @Query(value = "SELECT * FROM Drivers WHERE id IN (:nearbyDriverIds) AND vehicle_type = :vehicleType AND status = :status", nativeQuery = true)
    List<Driver> findByIdInAndVehicleTypeAndStatus(List<Integer> nearbyDriverIds,@Param("vehicleType")String vehicleType,@Param("status") String status);
    
}
