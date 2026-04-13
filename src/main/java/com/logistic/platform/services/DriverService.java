package com.logistic.platform.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logistic.platform.models.Booking;
import com.logistic.platform.models.BookingStatus;
import com.logistic.platform.models.Driver;
import com.logistic.platform.repository.BookingRepository;
import com.logistic.platform.repository.DriverRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EmailService emailService;

    public Optional<Driver> getDriver(int id) {
        return driverRepository.findById(id);
    }

    public boolean acceptBooking(int driverId, Booking booking) {
        Optional<Driver> driverOpt = driverRepository.findById(driverId);
        if (driverOpt.isPresent()) {
            Driver driver = driverOpt.get();
            if (driver.getCurrentJob() == null) {
                driver.setCurrentJob(booking);
                driver.setStatus("Accepted");
                driver.setAvailable(false);
                System.out.println("within acceptedBooking drivers" + driver);
                // driver.setId(6);
                driverRepository.save(driver);
                System.out.println("accepteded Booking " );
                return true;
            }
        }
        return false;
    }

    public boolean updateJobStatus(int driverId, String status) {
        Optional<Driver> driverOpt = driverRepository.findById(driverId);
        if (driverOpt.isPresent()) {
            Driver driver = driverOpt.get();
            if (driver.getCurrentJob() != null) {
                if ("Delivered".equals(status)) {
                    System.out.println("status changed");
                    driver.setStatus(status);
                    if ("Delivered".equals(status)) {
                        // bookingService.updateBookingStatus(driver.getCurrentJob().getId(), "Delivered");
                        driver.setCurrentJob(null);
                        driver.setStatus("Available");
                        driver.setAvailable(true);
                        
                    }
                driverRepository.save(driver);
                return true;
                }
            }
            return false;
        }
        return false;
    }

    public List<Driver> getActiveBookingDrivers()
    {
        return driverRepository.findByStatus("Accepted");
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public List<Booking> getLiveAssignments() {
        return bookingRepository.findAll().stream()
                .filter(booking -> booking.getDriver() != null)
                .filter(booking -> booking.getStatus() != null)
                .filter(booking -> booking.getStatus() == BookingStatus.UNDER_PROCESS
                        || booking.getStatus() == BookingStatus.PENDING)
                .toList();
    }

    public void sendDeliveryCompletionCode(int driverId, int bookingId) {
        Driver driver = getDriverOrThrow(driverId);
        Booking booking = getAssignedBookingOrThrow(driver, bookingId);

        if (booking.getUser() == null || booking.getUser().getEmail() == null || booking.getUser().getEmail().isBlank()) {
            throw new IllegalStateException("User email is not available for this booking.");
        }

        String verificationCode = String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10);

        booking.setDeliveryVerificationCodeHash(hashVerificationCode(verificationCode));
        booking.setDeliveryVerificationSentAt(LocalDateTime.now());
        booking.setDeliveryVerificationExpiresAt(expiryTime);
        bookingRepository.save(booking);

        String emailBody = """
                Hello %s,

                Your delivery for booking #%d is ready to be completed.

                Please share this verification code with the driver:
                %s

                This code expires at %s.

                If you did not expect this request, please contact support immediately.
                """.formatted(
                booking.getUser().getUsername(),
                booking.getId(),
                verificationCode,
                expiryTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        emailService.sendEmail(
                booking.getUser().getEmail(),
                "Delivery completion verification code",
                emailBody);
    }

    public void completeBookingWithVerification(int driverId, int bookingId, String verificationCode) {
        if (verificationCode == null || verificationCode.isBlank()) {
            throw new IllegalArgumentException("Verification code is required.");
        }

        Driver driver = getDriverOrThrow(driverId);
        Booking booking = getAssignedBookingOrThrow(driver, bookingId);

        if (booking.getDeliveryVerificationCodeHash() == null || booking.getDeliveryVerificationCodeHash().isBlank()) {
            throw new IllegalStateException("Send the verification code to the user before completing the booking.");
        }

        if (booking.getDeliveryVerificationExpiresAt() == null || booking.getDeliveryVerificationExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("The verification code has expired. Please send a new code.");
        }

        String incomingHash = hashVerificationCode(verificationCode.trim());
        if (!incomingHash.equals(booking.getDeliveryVerificationCodeHash())) {
            throw new IllegalArgumentException("Invalid verification code.");
        }

        booking.setStatus(BookingStatus.DELIVERED);
        booking.setDeliverAt(LocalDateTime.now());
        booking.setDeliveryVerificationCodeHash(null);
        booking.setDeliveryVerificationSentAt(null);
        booking.setDeliveryVerificationExpiresAt(null);
        bookingRepository.save(booking);

        driver.setCurrentJob(null);
        driver.setStatus("Available");
        driver.setAvailable(true);
        driverRepository.save(driver);
    }

    private Driver getDriverOrThrow(int driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found."));
    }

    private Booking getAssignedBookingOrThrow(Driver driver, int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        if (booking.getDriver() == null || booking.getDriver().getId() != driver.getId()) {
            throw new IllegalStateException("This booking is not assigned to the selected driver.");
        }

        if (booking.getStatus() == BookingStatus.DELIVERED) {
            throw new IllegalStateException("This booking is already completed.");
        }

        return booking;
    }

    private String hashVerificationCode(String verificationCode) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(verificationCode.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashedBytes);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Unable to generate verification hash.", exception);
        }
    }

    
}
