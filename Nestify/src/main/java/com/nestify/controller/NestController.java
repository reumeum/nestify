package com.nestify.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.nestify.entity.UserEntity;

import jakarta.servlet.http.HttpSession;

@Controller
public class NestController {
	
	@GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
		UserEntity user = (UserEntity) session.getAttribute("user");
        
        if (user != null) {
            model.addAttribute("user", user);
        } else {
            return "redirect:/signin";
        }
        
		return "nest/dashboard";
	}
}
