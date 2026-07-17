package in.kr.main.exceptions;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler({RoomAlreadyExistsException.class, EmailAlreadyExistsException.class ,UserAlreadyExistsException.class})
	public ResponseEntity<Map<String, Object>> AlreadyExistsException(Exception e){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("time", LocalDateTime.now());
		map.put("message", e.getMessage());
		return new ResponseEntity<Map<String,Object>>(map, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler({InvalidRoomIdException.class, AccessDeniedException.class, PasswordMismatchException.class, IllegalArgumentException.class, KeyNotExistsException.class, InvalidEmailException.class, InvalidOtpException.class, OtpOrTokenExpiredException.class, EmailNotVerifiedException.class, NullPointerException.class, InvalidTokenException.class})
	public ResponseEntity<Map<String, Object>> BadRequestException(Exception e){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("time", LocalDateTime.now());
		map.put("message", e.getMessage());
		return new ResponseEntity<Map<String,Object>>(map, HttpStatus.BAD_REQUEST);
	}
}
