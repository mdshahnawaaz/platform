package com.logistic.platform.Controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.logistic.platform.models.Package;
import com.logistic.platform.models.PackageStatus;
import com.logistic.platform.services.PackageService;

@Controller
@RequestMapping("/logistics/tracking")
public class PackageController {

    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }
    @GetMapping("/track")
    public String trackPage() {
        return "tracking_search";
    }
    
    @GetMapping("/search")
    public String searchTracking(@RequestParam("trackingId") int trackingId, Model model) {
        
        try{
        Package pkg = packageService.getPackageByTrackingId(trackingId);
            if (pkg != null) {
                List<PackageStatus> statusHistory = packageService.getPackageStatusHistory(trackingId);
                model.addAttribute("package", pkg);
                model.addAttribute("statusHistory", statusHistory);
                return "tracking_package"; // Redirect to details page
            }
            model.addAttribute("error", "Tracking ID not found. Please try again.");
            return "tracking_search";
        } catch(Exception ex){
        model.addAttribute("error", "Tracking ID not found. Please try again.");
        return "tracking_search";
        } 
    }

    @GetMapping("/{trackingId}")
    public String getTrackingPage(@PathVariable int trackingId, Model model) {
        Package pkg = packageService.getPackageByTrackingId(trackingId);
        List<PackageStatus> statusHistory = packageService.getPackageStatusHistory(trackingId);

        model.addAttribute("package", pkg);
        model.addAttribute("statusHistory", statusHistory);
        return "tracking_package";
    }

}
