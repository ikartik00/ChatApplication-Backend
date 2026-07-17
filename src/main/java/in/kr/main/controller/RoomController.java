package in.kr.main.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.kr.main.entity.Messages;
import in.kr.main.entity.Room;
import in.kr.main.exceptions.RoomAlreadyExistsException;
import in.kr.main.io.MessageResponse;
import in.kr.main.io.NewRoomRequest;
import in.kr.main.io.RoomResponse;
import in.kr.main.repository.RoomRepository;
import in.kr.main.service.RoomService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RoomController {
	private final RoomService roomService;
	
	
	@PostMapping("/create-room")
	public ResponseEntity<RoomResponse> createRoom(@RequestBody NewRoomRequest request){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		RoomResponse roomResponse = roomService.createRoom(request.getRoomId(), email);
		return new ResponseEntity<RoomResponse>(roomResponse, HttpStatus.CREATED);
	}
	
	@GetMapping("/room/{roomId}")
	public ResponseEntity<RoomResponse> getRoom(@PathVariable String roomId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		RoomResponse roomResponse = roomService.getRoom(roomId, email);
		return new ResponseEntity<RoomResponse>(roomResponse, HttpStatus.OK);
	}
	
	@GetMapping("/all-messages/{roomId}")
	public ResponseEntity<List<MessageResponse>> getAllMessages(@PathVariable String roomId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		List<MessageResponse> messages = roomService.getAllMessages(roomId, email);
		return new ResponseEntity<List<MessageResponse>>(messages, HttpStatus.OK);
	}
	
	@GetMapping("/getAllRoom")
	public ResponseEntity<List<RoomResponse>> getAllRoom(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedInUserEmail = auth.getName();
		List<RoomResponse> allRooms = roomService.getAllRoom(loggedInUserEmail);
		return new ResponseEntity<List<RoomResponse>>(allRooms, HttpStatus.OK);
	}
	
	@GetMapping("/getAllRoomsCreateOrJoin")
	public ResponseEntity<List<RoomResponse>> getAllRoomsJoinOrCreated(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedInUserEmail = auth.getName();
		List<RoomResponse> allRooms = roomService.getAllRoomsJoinOrCreated(loggedInUserEmail);
		return new ResponseEntity<List<RoomResponse>>(allRooms, HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/room/{roomId}")
	public ResponseEntity<?> deleteUser(@PathVariable String roomId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String loggedInUserEmail = auth.getName();
		roomService.deleteRoom(roomId, loggedInUserEmail);
		return ResponseEntity.status(204).build();
	}
}
