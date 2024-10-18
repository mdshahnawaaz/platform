package com.logistic.platform.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontenedController {
    
    @GetMapping("/websoc")
    public String showsoc()
    {
        return "websoc";
    }

    @GetMapping("/index")
    public String book()
    {
        return "book";
    }
}
