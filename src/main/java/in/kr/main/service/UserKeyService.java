package in.kr.main.service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import in.kr.main.entity.Room;
import in.kr.main.entity.RoomMembers;
import in.kr.main.entity.UserEntity;
import in.kr.main.entity.UserKey;
import in.kr.main.exceptions.AccessDeniedException;
import in.kr.main.exceptions.InvalidRoomIdException;
import in.kr.main.exceptions.KeyNotExistsException;
import in.kr.main.io.KeyRequest;
import in.kr.main.io.PrivateKeyResponse;
import in.kr.main.io.UserKeyResponse;
import in.kr.main.repository.RoomMemberRepository;
import in.kr.main.repository.RoomRepository;
import in.kr.main.repository.UserKeyRepository;
import in.kr.main.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserKeyService {
	private final UserRepository userRepository;
	private final UserKeyRepository keyRepository;
	private final RoomRepository roomRepository;
	private final RoomMemberRepository memberRepository;
	
	public UserKey uploadKey(String email, String publicKey, String encryptedPrivateKey, String iv, String salt) {
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not exists withn this email " + email));
		Optional<UserKey> userKeyy = keyRepository.findByUserId(userEntity.getUserId());
		if(userKeyy.isPresent()) {
			userKeyy.get().setPublicKey(publicKey);
			return keyRepository.save(userKeyy.get());
		}
		UserKey userKey = new UserKey();
		userKey.setUserId(userEntity.getUserId());
		userKey.setGeneratedAt(LocalDateTime.now());
		userKey.setPublicKey(publicKey);
		userKey.setEncryptedPrivateKey(encryptedPrivateKey);
		userKey.setIv(iv);
		userKey.setSalt(salt);
		return keyRepository.save(userKey);
	}

	public UserKey getPublicKey(String userId) {
		UserKey userKey = keyRepository.findByUserId(userId).orElseThrow(()-> new KeyNotExistsException("Key Not exists for this userId"));
		return userKey;
	}

	public List<UserKeyResponse> getAllKeysByRoomId(String email, String roomId) {
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not exists withn this email " + email));
		Room room = roomRepository.findByRoomId(roomId).orElseThrow(()-> new InvalidRoomIdException("Room Does not exists with this id " + roomId));
		if(!memberRepository.existsByUserIdAndRoomId(userEntity.getUserId(), roomId)) {
			throw new AccessDeniedException("You Don't have access to enter the room");
		}
		List<RoomMembers> roomMembers = memberRepository.findAllByRoomId(roomId);
		List<String> userIds = roomMembers.stream().map(member -> member.getUserId()).collect(Collectors.toList());
		List<UserKey> userIdsAndKeys = keyRepository.findByUserIdIn(userIds);
		return userIdsAndKeys.stream().map(key -> {
			return UserKeyResponse.builder()
					.userId(key.getUserId())
					.publicKey(key.getPublicKey())
					.build();
		}).collect(Collectors.toList());
	}

	public PrivateKeyResponse getPrivateKey(String email) {
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not exists withn this email " + email));
		UserKey userKey = keyRepository.findByUserId(userEntity.getUserId()).orElseThrow(()-> new KeyNotExistsException("Key Not exists with this userId " + userEntity.getUserId()));
		return PrivateKeyResponse.builder()
				.iv(userKey.getIv())
				.privateKey(userKey.getEncryptedPrivateKey())
				.salt(userKey.getSalt())
				.build();
	}
}
