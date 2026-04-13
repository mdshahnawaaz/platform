package com.logistic.platform.services;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logistic.platform.models.Booking;
import com.logistic.platform.models.BookingCreationResult;
import com.logistic.platform.models.BookingStatus;
import com.logistic.platform.models.Driver;
import com.logistic.platform.models.PricingQuote;
import com.logistic.platform.models.User;
import com.logistic.platform.repository.BookingRepository;
import com.logistic.platform.repository.UserRepository;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PricingService pricingService;

    @Autowired
    private MatchingService matchingService;

    public BookingCreationResult createBooking(int userId, double pickupLat, double pickupLon, double dropoffLat,double dropoffLon, String vehicleType) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        PricingQuote pricingQuote = pricingService.buildQuote(
                pickupLat,
                pickupLon,
                dropoffLat,
                dropoffLon,
                vehicleType,
                null);
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setPickuplat(pickupLat);
        booking.setPickuplon(pickupLon);
        booking.setDropoffLat(dropoffLat);
        booking.setDropoffLon(dropoffLon);
        booking.setVehicleType(vehicleType);
        booking.setEstimatedCost(pricingQuote.estimatedPrice());
        booking.setCreatedAt(LocalDateTime.now());
        booking.setStatus(BookingStatus.PENDING);
        booking = bookingRepository.save(booking);

        Driver dr=matchingService.findMatchingDriver(booking);
        System.out.println(booking.getId());

        if(dr!=null)
            booking.setStatus(BookingStatus.UNDER_PROCESS);
        
        booking.setDriver(dr);
        booking = bookingRepository.save(booking);
        return new BookingCreationResult(booking, pricingQuote);
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
        if(status.equalsIgnoreCase("under process"))
            booking.setStatus(BookingStatus.UNDER_PROCESS);
        else
            booking.setStatus(BookingStatus.DELIVERED);
            
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

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
