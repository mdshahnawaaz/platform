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
}
