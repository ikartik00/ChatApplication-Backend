package in.kr.main.controller;

import in.kr.main.service.FileUploadService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import in.kr.main.entity.MessageKey;
import in.kr.main.entity.Messages;
import in.kr.main.entity.Room;
import in.kr.main.entity.UserEntity;
import in.kr.main.exceptions.AccessDeniedException;
import in.kr.main.exceptions.InvalidRoomIdException;
import in.kr.main.io.MessageRequest;
import in.kr.main.io.MessageResponse;
import in.kr.main.io.TypingEvent;
import in.kr.main.repository.MessageKeyRepository;
import in.kr.main.repository.MessageRepository;
import in.kr.main.repository.RoomMemberRepository;
import in.kr.main.repository.RoomRepository;
import in.kr.main.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
	private final FileUploadService fileUploadService;
	private final RoomRepository roomRepository;
	private final MessageRepository messageRepository;
	private final UserRepository userRepository;
	private final RoomMemberRepository memberRepository;
	private final SimpMessagingTemplate messagingTemplate;
	private final MessageKeyRepository keyRepository;

	

	// Agar client /app/sendMessage/abc123 par message bheje to ye method call karo
	@MessageMapping("/sendMessage/{roomId}")
	// @Send To -> Method jo message return karega wo /topic/room/{roomId} par bhej
	// do
//	@SendTo("/topic/room/{roomId}")
	public void sendMessage(@DestinationVariable String roomId, @RequestBody MessageRequest request,
			Principal principal) {
		if (principal == null) {
			throw new AccessDeniedException("User is not authenticated");
		}
		String userEmail = principal.getName();

		UserEntity user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new UsernameNotFoundException("User not exist with this email" + userEmail));

		Room room = roomRepository.findByRoomId(roomId)
				.orElseThrow(() -> new InvalidRoomIdException("Room Not found with this id" + request.getRoomId()));

		if (!memberRepository.existsByRoomIdAndUserId(roomId, user.getUserId())) {
			throw new AccessDeniedException("User doesnot have access to message in this room");
		}

		Messages message = new Messages();
		message.setContent(request.getContent());
		message.setUser(user);
		message.setSentAt(LocalDateTime.now());
		message.setType(request.getType());
		message.setImageUrl(request.getImageUrl());
		message.setIv(request.getIv());
		message.setRoom(room);
		Messages DBMessage = messageRepository.save(message);
		
		Map<String, String> encryptedKeys = request.getEncryptedKeys();
		List<MessageKey> keyList = encryptedKeys
		.entrySet()
		.stream()
		.map(entry-> {
			MessageKey messageKey = new MessageKey();
			messageKey.setMessage(DBMessage);
			messageKey.setUserId(entry.getKey());
			messageKey.setEncryptedKeys(entry.getValue());
			return messageKey;
		}).collect(Collectors.toList());
		
		keyRepository.saveAll(keyList);
		
		List<String> memberIds = new ArrayList<>(encryptedKeys.keySet());
		List<UserEntity> allMembers = userRepository.findByUserIdIn(memberIds);

		Map<String, String> userIdToEmail = allMembers.stream()
		    .collect(Collectors.toMap(UserEntity::getUserId, 
		    						UserEntity::getEmail));

		// Ab loop mein sirf map lookup karo — DB query nahi
		for (Map.Entry<String, String> entry : encryptedKeys.entrySet()) {
		    String email = userIdToEmail.get(entry.getKey());
		    MessageResponse response = convertToResponse(DBMessage, entry.getValue());
		    messagingTemplate.convertAndSendToUser(email, "/queue/room/" + roomId, response);
		}		
	}

	@MessageMapping("/typing/{roomId}")
	public void typingIndicator(@DestinationVariable String roomId, @Payload TypingEvent event, Principal principal) {
		if (principal == null) {
			throw new AccessDeniedException("User is not authenticated");
		}
		String userEmail = principal.getName();
		UserEntity user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new UsernameNotFoundException("User not exist with this email" + userEmail));
		event.setRoomId(roomId);
		event.setUserId(user.getUserId());
		event.setName(user.getName());
		messagingTemplate.convertAndSend("/topic/room/"+roomId+"/typing", event);
	}

	private MessageResponse convertToResponse(Messages message, String encryptedKey) {
		UserEntity user = message.getUser();
		return MessageResponse.builder()
				.content(message.getContent())
				.imageUrl(message.getImageUrl())
				.senderImageUrl(user != null ? user.getImgUrl() : null)
				.type(message.getType())
				.senderName(user != null ? user.getName() : null)
				.senderUserId(user != null ? user.getUserId() : null)
				.myEncryptedKey(encryptedKey)
				.iv(message.getIv())
				.sentAt(message.getSentAt()).build();
	}

//	@PostMapping("/uploadImage")
//	public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
//		if (file != null && !file.isEmpty()) {
//			String fileName = UUID.randomUUID().toString() + "."
//					+ StringUtils.getFilenameExtension(file.getOriginalFilename());
//			Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
//			Files.createDirectories(uploadPath);
//			Path targetLocation = uploadPath.resolve(fileName);
//			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//			String imgUrl = "http://localhost:8080/api/v1/uploads/" + fileName;
//			return ResponseEntity.ok(Map.of("imgUrl", imgUrl));
//		} else {
//			return ResponseEntity.status(400).body(Map.of("message", "Image is Empty"));
//		}
//	}
	
	@PostMapping("/uploadImage")
	public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
		if (file != null && !file.isEmpty()) {
			try {
				String imgUrl = fileUploadService.uploadFile(file);
				return ResponseEntity.ok(Map.of("imgUrl", imgUrl));
			}catch(Exception e) {
				return ResponseEntity.status(400).body(Map.of("message", "Some Error Occured"));
			}
		} else {
			return ResponseEntity.status(400).body(Map.of("message", "Image is Empty"));
		}
	}
}
