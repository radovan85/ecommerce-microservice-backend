package com.radovan.spring.interceptors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.radovan.spring.exceptions.SuspendedUserException;
import com.radovan.spring.utils.CustomUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		// Pribavi autentifikaciju iz SecurityContext-a
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();

			// Provera da li je principal instance CustomUserDetails
			if (principal instanceof CustomUserDetails) {
				CustomUserDetails userDetails = (CustomUserDetails) principal;

				// Provera vrednosti "enabled" atributa
				Byte enabled = userDetails.getEnabled();
				if (enabled != null && enabled == 0) {
					throw new SuspendedUserException(new Error("User account is suspended"));
				}

				// Dodaj "Authorization" zaglavlje u odgovor sa trenutnim tokenom
				String authHeader = "Bearer " + authentication.getCredentials(); // ili koristi pravu metodu za token
				response.setHeader("Authorization", authHeader);
			}

		}

		return true;
	}
}
