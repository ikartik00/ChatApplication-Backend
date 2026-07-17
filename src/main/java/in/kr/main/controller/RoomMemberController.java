package in.kr.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import in.kr.main.io.RoomMemberRequest;
import in.kr.main.io.RoomMemberResponse;
import in.kr.main.service.RoomMemberService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RoomMemberController {
	private final RoomMemberService memberService;
	
	@PostMapping("/add-member")
	public ResponseEntity<RoomMemberResponse> addMember(@RequestBody RoomMemberRequest request){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roomCreatorEmail = auth.getName();
		
		String memberEmail = request.getEmail();
		RoomMemberResponse memberResponse = memberService.addMember(memberEmail, roomCreatorEmail, request.getRoomId());
		return new ResponseEntity<RoomMemberResponse>(memberResponse, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/delete/room/{roomId}/member/{userId}")
	public ResponseEntity<?> deleteMembers(@PathVariable String userId, @PathVariable String roomId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roomCreatorEmail = auth.getName();
		memberService.deleteMember(userId, roomId, roomCreatorEmail);
		return ResponseEntity.noContent().build();
	}
}
