package in.kr.main.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import in.kr.main.entity.Room;
import in.kr.main.entity.RoomMembers;
import in.kr.main.entity.UserEntity;
import in.kr.main.exceptions.AccessDeniedException;
import in.kr.main.exceptions.InvalidRoomIdException;
import in.kr.main.exceptions.UserAlreadyExistsException;
import in.kr.main.io.RoomMemberResponse;
import in.kr.main.repository.RoomMemberRepository;
import in.kr.main.repository.RoomRepository;
import in.kr.main.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomMemberService {
	private final UserRepository userRepository;
	private final RoomMemberRepository memberRepository;
	private final RoomRepository roomRepository;
	public RoomMemberResponse addMember(String memberEmail, String roomCreatorEmail, String roomId) {
		Room room = roomRepository.findByRoomId(roomId).orElseThrow(()-> new InvalidRoomIdException("Room Does not exists with this id " + roomId));
		UserEntity adminUser = userRepository.findByEmail(roomCreatorEmail).orElseThrow(()-> new UsernameNotFoundException("User not exist with this email " + roomCreatorEmail));
		 if(!memberRepository.existsByEmailAndRoleAndRoomId(adminUser.getEmail(), "ADMIN", roomId)) {
			 throw new AccessDeniedException("User doesn't have access to add the another User");
		 }
		
		UserEntity user = userRepository.findByEmail(memberEmail).orElseThrow(()-> new UsernameNotFoundException("User not exist with this email " + memberEmail));
		if(memberRepository.existsByRoomIdAndEmail(roomId, memberEmail)) {
			throw new UserAlreadyExistsException("User already exists in this room " + roomId);
		}
		RoomMembers member = convertToEntity(user, room.getRoomId());
		RoomMembers DBmember = memberRepository.save(member);
		return convertToResponse(DBmember);
	}
	
	private RoomMemberResponse convertToResponse(RoomMembers dBmember) {
		return RoomMemberResponse.builder()
				.email(dBmember.getEmail())
				.role(dBmember.getRole())
				.roomId(dBmember.getRoomId())
				.userId(dBmember.getUserId())
				.joinedAt(dBmember.getJoinedAt())
				.build();
	}
	
	private RoomMembers convertToEntity(UserEntity user, String roomId) {
		return RoomMembers.builder()
				.email(user.getEmail())
				.userId(user.getUserId())
				.roomId(roomId)
				.role("USER")
				.build();
	}

	@Transactional
	public void deleteMember(String userId, String roomId, String roomCreatorEmail) {
		UserEntity adminUser = userRepository.findByEmail(roomCreatorEmail).orElseThrow(()-> new UsernameNotFoundException("User not exist with this email " + roomCreatorEmail));
		Room room = roomRepository.findByRoomId(roomId).orElseThrow(()->new InvalidRoomIdException("Room Does not exists with this id"));
		if(!memberRepository.existsByUserIdAndRoomId(userId, roomId)) {
			throw new RuntimeException("User is not a member of group");
		}
		if(room.getCreatedBy().getUserId().equals(adminUser.getUserId())) {
			memberRepository.deleteByUserIdAndRoomId(userId, roomId);
		}else {
			throw new AccessDeniedException("Does'nt have access for deleting the user");
		}
	}
}
