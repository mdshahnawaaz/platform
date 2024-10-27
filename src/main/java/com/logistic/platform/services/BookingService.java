package com.logistic.platform.services;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import com.logistic.platform.Helper.DistanceCalculator;
import com.logistic.platform.models.Booking;
import com.logistic.platform.models.Driver;
import com.logistic.platform.models.User;
import com.logistic.platform.repository.BookingRepository;
import com.logistic.platform.repository.DriverRepository;
import com.logistic.platform.repository.UserRepository;

@Service
@Configuration
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PricingService pricingService;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private MatchingService matchingService;

    public Booking createBooking(int userId, double pickupLat, double pickupLon, double dropoffLat,double dropoffLon, String vehicleType) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        double distance = DistanceCalculator.calculateDistance(pickupLat, pickupLon, dropoffLat, dropoffLon);
        int k=driverRepository.countFreeDrivers();
        int demandFactor=1;
        if(k>10)
        {
            demandFactor=2;
        }
        else if(k>=5)
        {
            demandFactor=3;
        }

        BigDecimal estimatedCost= pricingService.calculatePrice(distance, vehicleType, demandFactor);
        
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setPickuplat(pickupLat);
        booking.setPickuplon(pickupLon);
        booking.setDropoffLat(dropoffLat);
        booking.setDropoffLon(dropoffLon);
        booking.setVehicleType(vehicleType);
        booking.setEstimatedCost(estimatedCost);
        booking.setStatus("PENDING");
        booking.setCreatedAt(LocalDateTime.now());
        Driver dr=matchingService.findMatchingDriver(booking);
        System.out.println(dr);
        booking.setDriver(dr);
        return bookingRepository.save(booking);
    }

    public Optional<Booking> getBooking(int id) {
        return bookingRepository.findById(id);
    }

    public Booking updateBookingStatus(int id, String status) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (!bookingOpt.isPresent()) {
            throw new RuntimeException("Booking not found");
        }

        Booking booking = bookingOpt.get();
        booking.setDeliverAt(LocalDateTime.now());
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    public Driver getBookingDriver(int BookingId)
    {
        Optional<Booking> k=bookingRepository.findById(BookingId);
        return  k.map(Booking::getDriver) // Assuming Booking has a getDriver() method
                                .orElse(null);
    }
    //  @KafkaListener(topics = AppConstants.LOCATION_UPDATE_TOPIC, groupId = AppConstants.GROUP_ID)
    // public void updatedLocation(List<Double>loc) {


    //     System.out.println(loc);

    // }
    public List<Booking> getDriverDetails(int driverId)
    {
        return bookingRepository.findByDriverId(driverId);
    }
}
