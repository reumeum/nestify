package com.nestify.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nestify.entity.UserEntity;
import com.nestify.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequiredArgsConstructor
public class MainController {
	private final UserService userService;

	@GetMapping("/")
	public String init() {
		return "redirect:/main";
	}

	@GetMapping("/main")
	public String main() {
		return "main/main";
	}

	@GetMapping("/signup")
	public String signupForm() {
		return "main/signup";
	}

	@GetMapping("/signin")
	public String signinForm(Model model) {
		return "main/signin";
	}

	@PostMapping("/signin")
	public String signin(@RequestParam(value = "email") String email, @RequestParam(value = "password") String password,
			Model model, HttpSession session) {
		log.debug("<<로그인>> : email=" + email);
		log.debug("<<로그인>> : password=" + password);

		// 사용자 인증 처리 로직
		boolean isAuthenticated = userService.authenticate(email, password);

		if (isAuthenticated) {
			// 인증이 성공한 경우: 홈 페이지 또는 대시보드로 리다이렉트
	        UserEntity user = userService.findByEmail(email);
	        session.setAttribute("user", user);
	        
	        log.debug("<<로그인 성공>> : " + user);
			
			return "redirect:/dashboard";
		} else {
			// 인증 실패: 에러 메시지를 모델에 추가하고 로그인 페이지로 다시 돌아가기
			model.addAttribute("errorMsg", "Invalid email or password");
			return "main/signin";
		}
	}

}
