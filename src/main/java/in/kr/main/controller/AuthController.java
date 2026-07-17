package in.kr.main.controller;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import in.kr.main.exceptions.InvalidEmailException;
import in.kr.main.io.AuthRequest;
import in.kr.main.io.AuthResponse;
import in.kr.main.io.ResetPasswordRequest;
import in.kr.main.service.AppUserDetailsService;
import in.kr.main.service.AuthService;
import in.kr.main.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
	private final AuthenticationManager authenticationManager;
	private final AppUserDetailsService appUserDetailsService;
	private final JwtService jwtService;
	private final AuthService authService;
	
	@PostMapping("/login")
	public ResponseEntity<?> loginRequest(@Valid @RequestBody AuthRequest request){
		try {
			authenticate(request.getEmail(), request.getPassword());
			UserDetails userDetails = appUserDetailsService.loadUserByUsername(request.getEmail());
			final String jwtToken = jwtService.generateToken(userDetails.getUsername());
			ResponseCookie responseCookie = ResponseCookie.from("jwt", jwtToken)
					.httpOnly(true)
					.path("/")
					.maxAge(Duration.ofDays(1))
					.sameSite("Lax")
					.build();
			return ResponseEntity
					.ok()
					.header(HttpHeaders.SET_COOKIE,responseCookie.toString())
					.body(new AuthResponse(request.getEmail(), jwtToken));
			
		}catch(BadCredentialsException e) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", "Invalid User Id and Password");
			map.put("errors", true);
			return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
		}catch(DisabledException e) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", "Account is Disabled");
			map.put("errors", true);
			return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
		}catch(Exception e) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", "Authentication Failed");
			map.put("errors", true);
			return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
		}
	}

	private void authenticate(String email, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(){
		ResponseCookie cookie = ResponseCookie.from("jwt", "")
				.httpOnly(true)
				.secure(false)
				.path("/")
				.maxAge(0)
				.sameSite("Lax")
				.build();
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("Logout Successfull");
	}
	
	@PostMapping("auth/send-otp")
	public void sendVerifyOtp(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		if(!email.contains("@") || !email.contains(".") || email.contains(" ")) {
			throw new InvalidEmailException("Please Enter Valid Email");
		}
		
		authService.sendVerifyOtp(email);
	}
	
	@PostMapping("/auth/verify-otp")
	public void verifyOtp(@RequestBody Map<String, String> request) {
		authService.verifyOtp(request);
	}
	
	@PostMapping("/forgot-password")
	public void sendForgotOtp(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		if(!email.contains("@") || !email.contains(".") || email.contains(" ")) {
			throw new InvalidEmailException("Please Enter Valid Email");
		}
		
		authService.sendResetLink(email);
	}
	
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
		authService.resetPassword(request);
		return ResponseEntity.ok().build();
	}
}
