package in.kr.main.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import in.kr.main.service.AppUserDetailsService;
import in.kr.main.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
	private final AppUserDetailsService appUserDetailsService;
	private final JwtService jwtService;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String path = request.getServletPath();
		if(path.equals("/login")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String authHeader = request.getHeader("Authorization");
		String email = null;
		String jwtToken = null;
		
		if(authHeader != null && authHeader.startsWith("Bearer")) {
			jwtToken = authHeader.substring(7);
		}
		
		if(jwtToken == null) {
			Cookie[] cookies = request.getCookies();
			if(cookies != null) {
				for(Cookie cookie : cookies) {
					if("jwt".equals(cookie.getName())) {
						jwtToken = cookie.getValue();
						break;
					}
				}
			}
		}
		
		if(jwtToken != null) {
			email = jwtService.extractUsername(jwtToken);
			if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = appUserDetailsService.loadUserByUsername(email);
				if(jwtService.validateToken(jwtToken, userDetails)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		}
		
		filterChain.doFilter(request, response);
	}
}
