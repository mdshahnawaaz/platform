package com.logistic.platform.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logistic.platform.models.AdminDashboardData;
import com.logistic.platform.models.Booking;
import com.logistic.platform.models.BookingStatus;
import com.logistic.platform.models.Driver;
import com.logistic.platform.models.DriverPerformanceView;
import com.logistic.platform.models.EtaAccuracySummary;
import com.logistic.platform.repository.BookingRepository;
import com.logistic.platform.repository.DriverRepository;
import com.logistic.platform.repository.UserRepository;

@Service
public class AdminService {

     @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EtaPredictionService etaPredictionService;

    // Fleet Management
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public List<Booking> getAllBooking()
    {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getSingleBooking(int id)
    {
         return bookingRepository.findById(id);
        
    }

    public Optional<Driver> getSingleDriver(int id)
    {
         return driverRepository.findById(id);
        
    }

    public AdminDashboardData getDashboardData() {
        List<Booking> bookings = bookingRepository.findAll();
        List<Driver> drivers = driverRepository.findAll();

        long totalBookings = bookings.size();
        long completedTrips = bookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.DELIVERED)
                .count();
        long activeTrips = bookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.UNDER_PROCESS)
                .count();
        long pendingBookings = bookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.PENDING)
                .count();
        long availableDrivers = drivers.stream().filter(Driver::isAvailable).count();
        long busyDrivers = drivers.size() - availableDrivers;
        long totalUsers = userRepository.count();

        LocalDate today = LocalDate.now();
        DateTimeFormatter labelFormatter = DateTimeFormatter.ofPattern("dd MMM");
        List<String> weeklyLabels = IntStream.rangeClosed(0, 6)
                .mapToObj(offset -> today.minusDays(6L - offset))
                .map(date -> date.format(labelFormatter))
                .toList();

        List<Long> weeklyBookingCounts = IntStream.rangeClosed(0, 6)
                .mapToObj(offset -> today.minusDays(6L - offset))
                .map(date -> bookings.stream()
                        .filter(booking -> booking.getCreatedAt() != null)
                        .filter(booking -> booking.getCreatedAt().toLocalDate().equals(date))
                        .count())
                .toList();

        List<BigDecimal> weeklyRevenue = IntStream.rangeClosed(0, 6)
                .mapToObj(offset -> today.minusDays(6L - offset))
                .map(date -> bookings.stream()
                        .filter(booking -> booking.getCreatedAt() != null)
                        .filter(booking -> booking.getCreatedAt().toLocalDate().equals(date))
                        .map(Booking::getEstimatedCost)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .toList();

        BigDecimal revenueLast7Days = weeklyRevenue.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageOrderValue = totalBookings == 0
                ? BigDecimal.ZERO
                : bookings.stream()
                        .map(Booking::getEstimatedCost)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(totalBookings), 2, RoundingMode.HALF_UP);

        double fulfillmentRate = totalBookings == 0
                ? 0
                : BigDecimal.valueOf(completedTrips * 100.0 / totalBookings)
                        .setScale(1, RoundingMode.HALF_UP)
                        .doubleValue();

        Map<String, Long> bookingStatusBreakdown = bookings.stream()
                .collect(Collectors.groupingBy(
                        booking -> booking.getStatus().getDisplayName(),
                        LinkedHashMap::new,
                        Collectors.counting()));

        List<Booking> recentBookings = bookings.stream()
                .sorted(Comparator.comparing(Booking::getCreatedAt, Comparator.nullsLast(LocalDateTime::compareTo)).reversed())
                .limit(6)
                .toList();

        EtaAccuracySummary etaAccuracy = etaPredictionService.buildAccuracySummary(bookings);

        Map<Integer, Long> completedTripsByDriver = bookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.DELIVERED)
                .filter(booking -> booking.getDriver() != null)
                .collect(Collectors.groupingBy(
                        booking -> booking.getDriver().getId(),
                        Collectors.counting()));

        Map<Integer, Driver> driversById = drivers.stream()
                .collect(Collectors.toMap(Driver::getId, Function.identity()));

        List<DriverPerformanceView> topDrivers = completedTripsByDriver.entrySet().stream()
                .map(entry -> {
                    Driver driver = driversById.get(entry.getKey());
                    if (driver == null) {
                        return null;
                    }
                    return new DriverPerformanceView(
                            driver.getId(),
                            driver.getName(),
                            driver.getVehicleType(),
                            driver.getStatus(),
                            driver.getRating(),
                            entry.getValue(),
                            driver.isAvailable());
                })
                .filter(driver -> driver != null)
                .sorted(Comparator.comparingLong(DriverPerformanceView::completedTrips).reversed())
                .limit(5)
                .toList();

        if (topDrivers.isEmpty()) {
            topDrivers = drivers.stream()
                    .sorted(Comparator.comparingInt(Driver::getRating).reversed())
                    .limit(5)
                    .map(driver -> new DriverPerformanceView(
                            driver.getId(),
                            driver.getName(),
                            driver.getVehicleType(),
                            driver.getStatus(),
                            driver.getRating(),
                            0,
                            driver.isAvailable()))
                    .toList();
        }

        return new AdminDashboardData(
                totalBookings,
                completedTrips,
                activeTrips,
                pendingBookings,
                availableDrivers,
                busyDrivers,
                totalUsers,
                revenueLast7Days,
                averageOrderValue,
                fulfillmentRate,
                weeklyLabels,
                weeklyBookingCounts,
                weeklyRevenue,
                bookingStatusBreakdown,
                etaAccuracy,
                recentBookings,
                topDrivers);
    }

    public Driver updateDriverStatus(int driverId, String status) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        driver.setStatus(status);
        return driverRepository.save(driver);
    }

    public int number_of_booking_driver(int id)
    {
        return bookingRepository.countByDriver(id);
    }

    // Data Analytics
    // public long getTotalTripsCompleted() {
    //     return bookingRepository.countByStatus("COMPLETED");
    // }

    // public double getAverageTripTime() {
    //     List<Booking> completedBookings = bookingRepository.findByStatus("COMPLETED");
    //     return completedBookings.stream()
    //             .mapToLong(booking -> Duration.between(booking.getCreatedAt(), booking.getDeliverAt()).toMinutes())
    //             .average()
    //             .orElse(0);
    // }

    // public Map<Object, Long> getDriverPerformance() {
    //     List<Booking> completedBookings = bookingRepository.findByStatus("COMPLETED");
    //     return completedBookings.stream()
    //             .collect(Collectors.groupingBy(
    //                     booking -> booking.getDriver().getId(),
    //                     Collectors.counting()
    //             ));
    // }
}
