package com.logistic.platform.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.logistic.platform.Helper.DistanceCalculator;
import com.logistic.platform.Helper.Helper;
import com.logistic.platform.models.Booking;
import com.logistic.platform.models.Driver;
import com.logistic.platform.repository.DriverRepository;

@Service
public class MatchingService {

    @Autowired
    private DriverRepository driverRepository;

    private static final String DRIVER_GEO_KEY = "drivers_location";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private Helper helper;

    @Autowired
    private DriverService driverService;

    public Driver findMatchingDriver(Booking booking) {

        double pickupLat = booking.getPickuplat();
        double pickupLon = booking.getPickuplon();
        String vehicleType = booking.getVehicleType();

        List<Integer> nearbyDriverIds = findNearbyDrivers(pickupLat,pickupLon, 100000.0); // 5.0 km radius

        List<Driver> eligibleDrivers = driverRepository.findByIdInAndVehicleTypeAndStatus(
        nearbyDriverIds, vehicleType, "Available");
        eligibleDrivers.sort((d1, d2) -> helper.calculateScore(d2, booking) - helper.calculateScore(d1, booking));

        Driver dr= eligibleDrivers.isEmpty() ? null : eligibleDrivers.get(0);

        if(dr!=null){
            boolean b=driverService.acceptBooking(dr.getId(), booking);
            if(b)return dr;
        }
        return dr;

    }

    // private List<Integer> findNearbyDrivers(double lat, double lon , double radius) {
    //     // GeoResults<GeoLocation<String>> radius1 = redisTemplate.opsForGeo()
    //     //     .radius(DRIVER_GEO_KEY, new Circle(new Point(lat, lon), new Distance(radius, Metrics.KILOMETERS)));
    //     // List<Integer> dd=radius1.getContent().stream()
    //     // .map(result -> result.getContent().getName()) // Get the driverId (assuming it's stored as String)
    //     // .map(Integer::valueOf)                         // Convert driverId from String to Integer
    //     // .collect(Collectors.toList());
    //     // System.out.println(dd);
    //     List<Driver>dd1=driverRepository.findByStatus("Available");
    //     List<Integer> dd=dd1.stream()                // Stream over the list of Driver objects
    //     .map(Driver::getId)            // Map each Driver to its driverId
    //     .collect(Collectors.toList());
    //     System.out.println(dd);
    //     return dd;
    // }
    private List<Integer> findNearbyDrivers(double pickupLat, double pickupLon, double radiusKm) {
        List<Driver> availableDrivers = driverRepository.findByStatus("Available");
        
        return availableDrivers.stream()
            .filter(driver -> isWithinRadius(pickupLat, pickupLon, driver.getDriverLat(), driver.getDriverLon(), radiusKm))
            .map(Driver::getId)
            .collect(Collectors.toList());
    }
    
    private boolean isWithinRadius(double lat1, double lon1, double lat2, double lon2, double radiusKm) {
        double distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);
        return distance <= radiusKm;
    }

    @Scheduled(fixedRate = 10000) // Update every 10 seconds
public void updateDriverLocations() {
    List<Driver> activeDrivers = driverRepository.findByStatus("Available");
    for (Driver driver : activeDrivers) {
        redisTemplate.opsForGeo().add(DRIVER_GEO_KEY, 
            new Point(driver.getDriverLat(), driver.getDriverLon()), String.valueOf(driver.getId()));
    }
}

}
