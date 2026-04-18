package com.logistic.platform.Controllers;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.logistic.platform.models.AdminDashboardData;
import com.logistic.platform.models.Booking;
import com.logistic.platform.models.BookingStatus;
import com.logistic.platform.models.User;
import com.logistic.platform.services.AdminService;
import com.logistic.platform.services.BookingService;
import com.logistic.platform.repository.UserRepository;

@Controller
public class FrontenedController {

    private static final String USER_SESSION_KEY = "loggedInUserId";

    private final AdminService adminService;
    private final UserRepository userRepository;
    private final BookingService bookingService;

    public FrontenedController(AdminService adminService, UserRepository userRepository, BookingService bookingService) {
        this.adminService = adminService;
        this.userRepository = userRepository;
        this.bookingService = bookingService;
    }
    
    @GetMapping("/hi")
    public String showsoc(HttpSession session, Model model)
    {
        Integer userId = (Integer) session.getAttribute(USER_SESSION_KEY);
        if (userId == null) {
            return "redirect:/portal/user";
        }
        model.addAttribute("loggedInUserId", userId);
        return "user_fro"; 
    }

    @GetMapping("/frontuser")
    public String book(Model model)
    {
        return "hello";
    }

    @GetMapping("/")
    public String front()
    {
        return "portal_home";
    }

    @GetMapping("/portal/user")
    public String userPortal(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute(USER_SESSION_KEY);
        if (userId == null) {
            return "user_login";
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            session.invalidate();
            model.addAttribute("errorMessage", "User session expired. Please log in again.");
            return "user_login";
        }

        AdminDashboardData dashboardData = adminService.getDashboardData();
        List<Booking> userBookings = bookingService.getUserBookings(userId).stream()
                .sorted(Comparator.comparing(Booking::getCreatedAt).reversed())
                .toList();
        List<Booking> activeBookings = userBookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.PENDING || booking.getStatus() == BookingStatus.UNDER_PROCESS)
                .toList();
        List<Booking> completedBookings = userBookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.DELIVERED)
                .toList();
        BigDecimal totalSpend = userBookings.stream()
                .map(Booking::getEstimatedCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("user", user);
        model.addAttribute("defaultUserId", userId);
        model.addAttribute("availableDrivers", dashboardData.availableDrivers());
        model.addAttribute("completedTrips", dashboardData.completedTrips());
        model.addAttribute("activeTrips", dashboardData.activeTrips());
        model.addAttribute("fulfillmentRate", dashboardData.fulfillmentRate());
        model.addAttribute("userBookings", userBookings);
        model.addAttribute("activeUserBookings", activeBookings);
        model.addAttribute("completedUserBookings", completedBookings);
        model.addAttribute("userTotalSpend", totalSpend);
        return "user_view";
    }

    @PostMapping("/portal/user/login")
    public String loginUser(
            @RequestParam int userId,
            @RequestParam String email,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        return userRepository.findById(userId)
                .filter(user -> user.getEmail() != null)
                .filter(user -> user.getEmail().equalsIgnoreCase(email.trim()))
                .map(user -> {
                    session.setAttribute(USER_SESSION_KEY, user.getId());
                    return "redirect:/portal/user";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Invalid user ID or email.");
                    return "redirect:/portal/user";
                });
    }

    @PostMapping("/portal/user/logout")
    public String logoutUser(HttpSession session) {
        session.removeAttribute(USER_SESSION_KEY);
        return "redirect:/portal/user";
    }

    @GetMapping("/portal/admin")
    public RedirectView adminPortal() {
        return new RedirectView("/logistics/admin/dashboard");
    }

    @GetMapping("/portal/driver")
    public RedirectView driverPortal() {
        return new RedirectView("/logistics/drivers/portal");
    }
    @GetMapping("/logistics/global_reach")
    public String global_Reach() {
        return "global_reach";
    }

    @GetMapping("/logistics/secure_package")
    public String secure_Package() {
        return "secure_package";
    }

    @GetMapping("/logistics/secure_handle")
    public String secure_Handle() {
        return "secure_handle";
    }

    @GetMapping("/logistics/business_int")
    public String business_Int() {
        return "business_int";
    }

    @GetMapping("/logistics/eco_friendly")
    public String eco_Friendlyt() {
        return "eco_friendly";
    }
    
}
