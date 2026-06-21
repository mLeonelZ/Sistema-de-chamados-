package com.helpdesk.config;

import com.helpdesk.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	public JwtAuthFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain
	) throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization");
		final String jwt;

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		jwt = authHeader.substring(7);

		try {
			Claims claims = jwtService.parseToken(jwt);
			String userId = claims.getSubject();

			if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				String role = claims.get("role", String.class);
				java.util.List<org.springframework.security.core.GrantedAuthority> authorities = new ArrayList<>();
				if (role != null) {
					authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role));
				}
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						userId,
						null,
						authorities
				);
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		} catch (JwtException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\": \"Invalid token\"}");
			return;
		}

		filterChain.doFilter(request, response);
	}
}
