package in.kr.main.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.kr.main.io.ChangePasswordRequest;
import in.kr.main.io.MemberResponseforUserRooms;
import in.kr.main.io.RoomResponse;
import in.kr.main.io.UserRegisterRequest;
import in.kr.main.io.UserResponse;
import in.kr.main.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	@PostMapping("/register")
	public ResponseEntity<UserResponse> userRegister(@RequestBody UserRegisterRequest request) {
		UserResponse userResponse= userService.userRegister(request);
		return new ResponseEntity<UserResponse>(userResponse, HttpStatus.CREATED);
	}
	
	@GetMapping("/profile")
	public ResponseEntity<UserResponse> getProfile(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		UserResponse response = userService.getProfile(email);
		return new ResponseEntity<UserResponse>(response, HttpStatus.OK);
	}
	
	@GetMapping("/search")
	public ResponseEntity<List<UserResponse>> searchByEmail(@RequestParam String email){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedInUserEmail = auth.getName();
		List<UserResponse> users = userService.searchByEmail(email, loggedInUserEmail);
		return new ResponseEntity<List<UserResponse>>(users, HttpStatus.OK);
	}
	
	@GetMapping("/getAllUsers")
	public ResponseEntity<List<MemberResponseforUserRooms>> getAllUsers(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedInUserEmail = auth.getName();
		List<MemberResponseforUserRooms> users = userService.getAllUsers(loggedInUserEmail);
		return new ResponseEntity<List<MemberResponseforUserRooms>>(users, HttpStatus.OK);
	}
	
	@PostMapping("/upload")
	public ResponseEntity<UserResponse> editProfile(@RequestParam String name, @RequestParam(required = false) MultipartFile image) throws IOException{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedInUserEmail = auth.getName();
		UserResponse response = userService.editProfile(name, image, loggedInUserEmail);
		return new ResponseEntity<UserResponse>(response, HttpStatus.OK);
	}
	
	@DeleteMapping("/deleteAccount/user")
	public ResponseEntity<?> deleteAccount(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedInUserEmail = auth.getName();
		userService.deleteAccount(loggedInUserEmail);
		return ResponseEntity.status(204).build();
	}
	
	@PutMapping("/change-password")
	public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedInUserEmail = auth.getName();
		userService.changePassword(loggedInUserEmail, request);
		return ResponseEntity.status(200).build();
	}
}
