package com.logistic.platform.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.logistic.platform.models.AdminDashboardData;
import com.logistic.platform.services.AdminService;

@Controller
public class FrontenedController {

    private final AdminService adminService;

    public FrontenedController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    @GetMapping("/hi")
    public String showsoc()
    {
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
    public String userPortal(@RequestParam(defaultValue = "1") int userId, Model model) {
        AdminDashboardData dashboardData = adminService.getDashboardData();
        model.addAttribute("defaultUserId", userId);
        model.addAttribute("availableDrivers", dashboardData.availableDrivers());
        model.addAttribute("completedTrips", dashboardData.completedTrips());
        model.addAttribute("activeTrips", dashboardData.activeTrips());
        model.addAttribute("fulfillmentRate", dashboardData.fulfillmentRate());
        return "user_view";
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
