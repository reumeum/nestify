package com.nestify.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequiredArgsConstructor
public class MainController {
	
	@GetMapping("/")
	public String init() {
		return "redirect:/main";
	}
	
	@GetMapping("/main")
	public String main(Model model) {
		log.debug("<<main 컨트롤러 진입>>");
		return "main/main";
	}
}
