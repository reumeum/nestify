package com.nestify.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NestController {
	@GetMapping("/dashboard")
	public String main() {
		return "nest/dashboard";
	}
}
