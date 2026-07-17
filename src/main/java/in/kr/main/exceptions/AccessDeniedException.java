package in.kr.main.exceptions;

public class AccessDeniedException extends RuntimeException {
	public AccessDeniedException(String message) {
		super(message);
	}
}
