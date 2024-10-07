package com.nestify.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String requestURI = request.getRequestURI();
		log.debug("[[[Login Check Interceptor]]] : " + requestURI);
		
		HttpSession session = request.getSession();
		
		if (session == null || session.getAttribute("user") == null) {
			//로그인 되지 않음
			log.debug("[[[Login Check Interceptor]]] : 미인증 사용자 요청");
			
			//signup page로 redirect
			response.sendRedirect("/signin");
			return false;
		}
		
		return true;
	}
}
