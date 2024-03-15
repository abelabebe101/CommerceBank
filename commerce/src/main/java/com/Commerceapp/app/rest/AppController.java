package com.Commerceapp.app.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import java.security.SecureRandom;


@Controller
public class AppController {
	
	@Autowired
	private UserRepository repo;

	
	@GetMapping("/")
	public String viewHomePage() {
		return "index";
		
	}
	
	@GetMapping("/register")
	public String showSignUpForm(Model model) {
	    model.addAttribute("user", new User());
	     
	    return "signup_form";
	}
	
	@GetMapping("/activate")
	public String showactivateform(Model model) {
	    model.addAttribute("user", new User());
	     
	    return "activate_form";
	}
	
	@GetMapping("/login")
	public String showSignInForm(Model model) {
	    model.addAttribute("user", new User());
	     
	    return "loginpage";
	}
	
	@PostMapping("/process_register")
	public String processRegistration(User user) {
	    user.setAccountNum();
		repo.save(user);	     
	    return "register_success";
	      
	}
	
	@PostMapping("/process_dashboard")
	public String showDash(Model model) {
	     
	    return "dashboard";
	}
  
   

}
