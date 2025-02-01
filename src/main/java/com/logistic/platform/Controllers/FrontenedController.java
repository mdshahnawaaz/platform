package com.logistic.platform.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class FrontenedController {
    
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
        return "user_view";
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
