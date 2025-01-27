package com.logistic.platform.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.logistic.platform.models.Contact;
import com.logistic.platform.models.Status;
import com.logistic.platform.repository.ContactRepository;


@Controller
@RequestMapping("/logistics/contact")
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @PostMapping("/submitcontact")
    public String submitContactForm( @RequestParam("name") String name,
        @RequestParam("email") String email,
        @RequestParam("message") String message)
    {
        Contact c=new Contact();
        c.setEmail(email);
        c.setMessage(message);
        c.setName(name);
        c.setStatus(Status.RESOLVED);
        System.out.println(c);
        contactRepository.save(c);
        return "thank_you";
    }

    @GetMapping("/showContactMessage")
    public String showContactForm(Model model)
    {
        List<Contact>allMessage=contactRepository.getAll();
        model.addAttribute("contacts",allMessage);
        return "admin_contact_page";
    }

    @GetMapping("/contact")
    public String contactPage() {
        return "contact_page";
    }
    

}
