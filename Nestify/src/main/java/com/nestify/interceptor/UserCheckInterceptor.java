package com.nestify.interceptor;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.servlet.HandlerInterceptor;

import com.nestify.entity.UserEntity;
import com.nestify.service.NestService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UserCheckInterceptor implements HandlerInterceptor {

	private final NestService nestService;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
	        throws Exception {

	    // 요청 URI를 로깅
	    String requestURI = request.getRequestURI();
	    log.debug("[[[User Check Interceptor]]] : " + requestURI);

	    // 세션에서 사용자 정보를 가져옴
	    HttpSession session = request.getSession();
	    UserEntity user = (UserEntity) session.getAttribute("user");

	    // 로그인되지 않은 사용자는 로그인 페이지로 리다이렉트
	    if (user == null) {
	        response.sendRedirect("/signin");
	        return false;
	    }

	    // 리소스가 collection 또는 bookmark 관련일 때
	    if (requestURI.startsWith("/api/v1/collection/") || requestURI.startsWith("/api/v1/bookmark/")) {
	        
	        // URI에서 리소스 ID 추출
	        Long resourceId = extractResourceIdFromUri(requestURI);
	        // URI에서 리소스 타입 추출
	        String resourceType = extractResourceTypeFromUri(requestURI);

	        // 리소스 소유자 확인
	        if (resourceId != null && !userOwnsResource(user.getUserId(), resourceId, resourceType)) {
	            log.debug("[[[User Check Interceptor]]] : User does not own this resource");
	            // 권한이 없는 경우 403 Forbidden 응답 반환
	            sendForbiddenError(response);
	            return false;
	        }

	    } else if (requestURI.startsWith("/api/v1/collections/") || requestURI.startsWith("/api/v1/bookmarks/")) {
	        
	        // URI에서 userId 추출
	        Long userId = extractUserIdFromUri(requestURI);

	        // 현재 로그인한 사용자와 URI의 userId가 일치하지 않는 경우
	        if (!userId.equals(user.getUserId())) {
	            log.debug("[[[User Check Interceptor]]] : User does not own this resource");
	            // 권한이 없는 경우 403 Forbidden 응답 반환
	            sendForbiddenError(response);
	            return false;
	        }
	    }

	    // 모든 검사를 통과하면 요청을 처리하도록 허용
	    log.debug("[[[User Check Interceptor]]] : User checked");
	    return true;
	}
	
	/**
	 * 403 Forbidden 에러를 반환하는 메서드
	 * 
	 * @param response HttpServletResponse 객체
	 * @throws IOException 예외 처리
	 */
	private void sendForbiddenError(HttpServletResponse response) throws IOException {
	    response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to access this resource.");
	}
	
	/**
	 * URI에서 리소스 타입(bookmark 또는 collection)을 추출하는 메서드
	 * 
	 * @param uri 요청 URI
	 * @return 리소스 타입 (예: bookmark 또는 collection)
	 */
	private String extractResourceTypeFromUri(String uri) {
	    // URI에서 'bookmark' 또는 'collection'을 추출하는 정규 표현식
	    Pattern pattern = Pattern.compile("/api/v1/(bookmark|collection)/\\d+");
	    Matcher matcher = pattern.matcher(uri);
	    if (matcher.find()) {
	        return matcher.group(1);  // bookmark 또는 collection 반환
	    }
	    return null;
	}

	/**
	 * URI에서 리소스 ID를 추출하는 메서드
	 * 
	 * @param uri 요청 URI
	 * @return 추출된 리소스 ID, 없을 경우 null
	 */
	private Long extractResourceIdFromUri(String uri) {
		Pattern pattern = Pattern.compile("/api/v1/(bookmark|collection)/(\\d+)");
		Matcher matcher = pattern.matcher(uri);

		if (matcher.find()) {
			return Long.parseLong(matcher.group(2));
		}

		return null;
	}

	/**
	 * URI에서 userId를 추출하는 메서드
	 * 
	 * @param uri 요청 URI
	 * @return 추출된 userId, 없을 경우 null
	 */
	private Long extractUserIdFromUri(String uri) {
		Pattern pattern = Pattern.compile("/api/v1/(bookmarks|collections)/(\\d+)");
		Matcher matcher = pattern.matcher(uri);

		if (matcher.find()) {
			return Long.parseLong(matcher.group(2));
		}

		return null;
	}

	/**
	 * 사용자가 특정 리소스의 소유자인지 확인하는 메서드
	 * 
	 * @param userId 사용자 ID
	 * @param resourceId 리소스 ID
	 * @param resourceType 리소스 타입 (예: bookmark 또는 collection)
	 * @return 리소스 소유 여부 (true: 소유, false: 소유 아님)
	 */
	private boolean userOwnsResource(Long userId, Long resourceId, String resourceType) {
	    // 리소스 타입에 따라 소유자 확인
	    if ("bookmark".equals(resourceType)) {
	        return nestService.isUserOwnerOfBookmark(userId, resourceId);
	    } else if ("collection".equals(resourceType)) {
	        return nestService.isUserOwnerOfCollection(userId, resourceId);
	    }
	    return false;
	}
}
