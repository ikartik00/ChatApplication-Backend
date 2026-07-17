package in.kr.main.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import in.kr.main.entity.MessageKey;
import in.kr.main.entity.Messages;
import in.kr.main.entity.Room;
import in.kr.main.entity.RoomMembers;
import in.kr.main.entity.UserEntity;
import in.kr.main.exceptions.AccessDeniedException;
import in.kr.main.exceptions.InvalidRoomIdException;
import in.kr.main.exceptions.RoomAlreadyExistsException;
import in.kr.main.io.MessageResponse;
import in.kr.main.io.RoomResponse;
import in.kr.main.repository.RoomMemberRepository;
import in.kr.main.repository.RoomRepository;
import in.kr.main.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {
	private final RoomRepository roomRepository;
	private final UserRepository userRepository;
	private final RoomMemberRepository memberRepository;
	
	
	public RoomResponse createRoom(String roomId, String email) {
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not exists withn this email " + email));
		if(roomRepository.existsByRoomId(roomId.trim())) {
			throw new RoomAlreadyExistsException("Room Already Exists");
		}
		Room room = new Room();
		room.setRoomId(roomId);
		room.setCreatedBy(userEntity);
		
		userEntity.getRooms().add(room);
		
		Room DBroom = roomRepository.save(room);
		RoomMembers member = new RoomMembers();
		member.setEmail(userEntity.getEmail());
		member.setRole("ADMIN");
		member.setRoomId(DBroom.getRoomId());
		member.setUserId(userEntity.getUserId());
		memberRepository.save(member);
		return convertToResponse(DBroom);
	}
	
	public RoomResponse getRoom(String roomId, String email) {
		Room room = roomRepository.findByRoomId(roomId).orElseThrow(()-> new InvalidRoomIdException("Room Does not exists with this id " + roomId));
		
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not exists withn this email " + email));
		if(!memberRepository.existsByRoomIdAndUserId(roomId, userEntity.getUserId())) {
			throw new AccessDeniedException("User Does not have access to enter the room contact the admin of the room");
		}
		return convertToResponse(room);
	}
	
	private RoomResponse convertToResponse(Room room) {
		Integer members = memberRepository.countByRoomId(room.getRoomId());
		return RoomResponse.builder()
				.adminName(room.getCreatedBy().getName())
				.userId(room.getCreatedBy().getUserId())
				.createdAt(room.getCreatedBy().getCreatedAt())
				.roomId(room.getRoomId())
				.members(members)
				.build();
	}

	public List<MessageResponse> getAllMessages(String roomId, String email) {
		Room room = roomRepository.findByRoomId(roomId).orElseThrow(()-> new InvalidRoomIdException("Room Does not exists with this id " + roomId));
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not exists withn this email " + email));
		if(!memberRepository.existsByRoomIdAndUserId(roomId, userEntity.getUserId())) {
			throw new AccessDeniedException("User Does not have access to enter the room contact the admin of the room");
		}
		List<Messages> allMessages = room.getMessages();
		
		return allMessages.stream().map((message)->convertToResponse(message, userEntity)).collect(Collectors.toList());
	}
	
	private MessageResponse convertToResponse(Messages message,UserEntity user) {
		String myEncryptedKey = message.getMessageKeys().stream()
		        .filter(key -> key.getUserId().equals(user.getUserId()))
		        .map(MessageKey::getEncryptedKeys)
		        .findFirst()
		        .orElse(null);
		UserEntity messageUser = message.getUser();
		return MessageResponse.builder()
				.content(message.getContent())
				.imageUrl(message.getImageUrl())
				.senderImageUrl(messageUser!= null ? messageUser.getImgUrl():null)
				.type(message.getType())
				.senderName(messageUser!= null ? messageUser.getName():null)
				.senderUserId(messageUser!= null ? messageUser.getUserId():null)
				.sentAt(message.getSentAt())
				.myEncryptedKey(myEncryptedKey)
				.iv(message.getIv())
				.build();
	}
	
	public List<RoomResponse> getAllRoom(String loggedInUserEmail) {
		UserEntity user = userRepository.findByEmail(loggedInUserEmail).orElseThrow(()-> new UsernameNotFoundException("User not found with this email " + loggedInUserEmail));
		List<Room> rooms = user.getRooms();
		return rooms.stream().map(this::convertToResponse).collect(Collectors.toList());
	}

	public List<RoomResponse> getAllRoomsJoinOrCreated(String loggedInUserEmail) {
		UserEntity userEntity = userRepository.findByEmail(loggedInUserEmail).orElseThrow(()-> new UsernameNotFoundException("User not exists withn this email " + loggedInUserEmail));
		List<Room> userRoomsCreateOrJoin = memberRepository.findByUserId(userEntity.getUserId());
		return userRoomsCreateOrJoin.stream().map(this::convertToResponse).collect(Collectors.toList());
	}

	@Transactional
	public void deleteRoom(String roomId, String loggedInUserEmail) {
		UserEntity user = userRepository.findByEmail(loggedInUserEmail).orElseThrow(()-> new UsernameNotFoundException("User not found with this email " + loggedInUserEmail));
		Room room = roomRepository.findByRoomId(roomId).orElseThrow(()-> new InvalidRoomIdException("Room Doesn't exists with this id"));
		if(!(room.getCreatedBy().getUserId().equals(user.getUserId()))) {
			throw new AccessDeniedException("User Does'nt have access to delete the room");
		}
		roomRepository.delete(room);
		memberRepository.deleteAllByRoomId(roomId);
	}
}
