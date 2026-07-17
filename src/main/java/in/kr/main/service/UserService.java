package in.kr.main.service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import in.kr.main.entity.EmailVerification;
import in.kr.main.entity.Room;
import in.kr.main.entity.RoomMembers;
import in.kr.main.entity.UserEntity;
import in.kr.main.entity.UserKey;
import in.kr.main.exceptions.AccessDeniedException;
import in.kr.main.exceptions.EmailAlreadyExistsException;
import in.kr.main.exceptions.EmailNotVerifiedException;
import in.kr.main.exceptions.KeyNotExistsException;
import in.kr.main.exceptions.PasswordMismatchException;
import in.kr.main.io.ChangePasswordRequest;
import in.kr.main.io.MemberResponseforUserRooms;
import in.kr.main.io.RoomResponse;
import in.kr.main.io.UserRegisterRequest;
import in.kr.main.io.UserResponse;
import in.kr.main.repository.EmailVerificationRepository;
import in.kr.main.repository.MessageRepository;
import in.kr.main.repository.RoomMemberRepository;
import in.kr.main.repository.RoomRepository;
import in.kr.main.repository.UserKeyRepository;
import in.kr.main.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RoomMemberRepository memberRepository;
	private final RoomMemberRepository roomMemberRepository;
	private final RoomRepository roomRepository;
	private final MessageRepository messageRepository;
	private final UserKeyService userKeyService;
	private final UserKeyRepository userKeyRepository;
	private final EmailVerificationRepository verificationRepository;
	private final FileUploadService fileUploadService;
	
	@Transactional
	public UserResponse userRegister(UserRegisterRequest request) {
		if(userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("User already exists with this email " + request.getEmail());
		}
		Optional<EmailVerification> emailVerification = verificationRepository.findByEmail(request.getEmail());
		if(emailVerification.isPresent()) {
			if(!emailVerification.get().getVerified()) {
				throw new EmailNotVerifiedException("Please First Verify the email");
			}
		}else {
			throw new EmailNotVerifiedException("Email is Not Verified");
		}
		UserEntity userEntity = convertToEntity(request);
		UserEntity DBUser = userRepository.save(userEntity);
		userKeyService.uploadKey(DBUser.getEmail(), request.getPublicKey(), request.getEncryptedPrivateKey(), request.getIv(), request.getSalt());
		
		return convertToResponse(DBUser);
	}
	
	public UserEntity convertToEntity(UserRegisterRequest request) {
		return UserEntity.builder()
			.email(request.getEmail())
			.name(request.getName())
			.password(passwordEncoder.encode(request.getPassword()))
			.userId(UUID.randomUUID().toString())
			.build();
	}
	
	public UserResponse convertToResponse(UserEntity userEntity) {
		return UserResponse.builder()
			.email(userEntity.getEmail())
			.name(userEntity.getName())
			.createdAt(userEntity.getCreatedAt())
			.userId(userEntity.getUserId())
			.imgUrl(userEntity.getImgUrl())
			.build();
	}

	public UserResponse getProfile(String email) {
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User Not found with this email" + email));
		return convertToResponse(userEntity);
	}

	public List<UserResponse> searchByEmail(String searchEmail, String loggedInUserEmail) {
		if(!userRepository.existsByEmail(loggedInUserEmail)) {
			throw new AccessDeniedException("Not Access  to Users");
		}
		List<UserEntity> users = userRepository.findByEmailContainingIgnoreCase(searchEmail);
		return users
				.stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	public List<MemberResponseforUserRooms> getAllUsers(String loggedInUserEmail) {
		UserEntity userEntity = userRepository.findByEmail(loggedInUserEmail).orElseThrow(()-> new UsernameNotFoundException("User Not found with this email" + loggedInUserEmail));
		return memberRepository.findAllUsersRooms(userEntity.getUserId());
	}

	public UserResponse editProfile(String name, MultipartFile image, String loggedInUserEmail) throws IOException {
		UserEntity userEntity = userRepository.findByEmail(loggedInUserEmail).orElseThrow(()-> new UsernameNotFoundException("User Not found with this email" + loggedInUserEmail));
		if(image != null && !image.isEmpty()) {
			try {
				String imgUrl = fileUploadService.uploadFile(image);
				userEntity.setImgUrl(imgUrl);
			}catch(Exception e) {
				throw new RuntimeException("Image Not Uploadeed");
			}
		}
		
		if(name != null &&  !userEntity.getName().equals(name)) {
			userEntity.setName(name);
		}
		userRepository.save(userEntity);
		return convertToResponse(userEntity);
	}

	@Transactional
	public void deleteAccount(String loggedInUserEmail) {
		UserEntity userEntity = userRepository.findByEmail(loggedInUserEmail).orElseThrow(()-> new UsernameNotFoundException("User Not found with this email" + loggedInUserEmail));
		messageRepository.setUserToNull(userEntity.getUserId());
		List<Room> allRooms = userEntity.getRooms();
		for(Room room : allRooms) {
			List<RoomMembers> roomMembers = roomMemberRepository.findOtherMembers(userEntity.getUserId(), room.getRoomId());
			if(!roomMembers.isEmpty()) {
				String userId = roomMembers.getFirst().getUserId();
				UserEntity user = userRepository.findByUserId(userId).orElseThrow(()->new UsernameNotFoundException("User not found with this id " + userId));
				room.setCreatedBy(user);
				roomRepository.save(room);
				RoomMembers member = roomMembers.getFirst();
				member.setRole("ADMIN");
				roomMemberRepository.save(member);
			}else {
				roomMemberRepository.deleteAllByRoomId(room.getRoomId());
				roomRepository.delete(room);
			}
		}
		roomMemberRepository.deleteAllByUserId(userEntity.getUserId());
		userRepository.delete(userEntity);
	}

	@Transactional
	public void changePassword(String loggedInUserEmail, ChangePasswordRequest request) {
		UserEntity userEntity = userRepository.findByEmail(loggedInUserEmail).orElseThrow(()-> new UsernameNotFoundException("User Not found with this email" + loggedInUserEmail));
		if (passwordEncoder.matches(request.getNewPassword(), userEntity.getPassword())) {
	        throw new IllegalArgumentException("New password cannot be the same as your current password");
	    }
		if(passwordEncoder.matches(request.getOldPassword(), userEntity.getPassword())) {
			userEntity.setPassword(passwordEncoder.encode(request.getNewPassword()));
			UserKey userKey = userKeyRepository.findByUserId(userEntity.getUserId()).orElseThrow(()->new KeyNotExistsException("Key Not exists with this user id : " + userEntity.getUserId()));
			userKey.setEncryptedPrivateKey(request.getEncryptedKey());
			userKey.setIv(request.getIv());
			userKey.setSalt(request.getSalt());
			userKeyRepository.save(userKey);;
			userRepository.save(userEntity);		
		}else {
			throw new PasswordMismatchException("Old Password is Invalid");
		}
	}
}
