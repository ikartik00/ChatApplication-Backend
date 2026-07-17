package in.kr.main.controller;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import in.kr.main.entity.UserKey;
import in.kr.main.io.KeyRequest;
import in.kr.main.io.PrivateKeyResponse;
import in.kr.main.io.UserKeyResponse;
import in.kr.main.service.UserKeyService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserKeyController {
	private final UserKeyService userKeyService;
//	@PostMapping("/keys")
//	public ResponseEntity<?> uploadKey(@RequestBody KeyRequest keyRequest) {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		String email = auth.getName();
//		userKeyService.uploadKey(email, keyRequest.getPublicKey());
//		return ResponseEntity.status(201).body("Public Key Saved Successfully");
//	}
	
//	@GetMapping("/keys/{userId}")
//	public ResponseEntity<Map<String, String>> getPublicKey(@PathVariable String userId){
//		UserKey userKey = userKeyService.getPublicKey(userId);
//		return new ResponseEntity<Map<String,String>>(Map.of("publicKey", userKey.getPublicKey()), HttpStatus.OK);
//	}
	
	@GetMapping("/keys/{roomId}")
	public ResponseEntity<List<UserKeyResponse>> getAllKeysByRoomId(@PathVariable String roomId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		System.out.println(roomId);
		List<UserKeyResponse> userIdAndKeys = userKeyService.getAllKeysByRoomId(email, roomId);
		return new ResponseEntity<List<UserKeyResponse>>(userIdAndKeys, HttpStatus.OK);
	}
	
	@GetMapping("/privateKey/user")
	public ResponseEntity<PrivateKeyResponse> getPrivateKey(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		return new ResponseEntity<PrivateKeyResponse>(userKeyService.getPrivateKey(email), HttpStatus.OK);
	}
}
