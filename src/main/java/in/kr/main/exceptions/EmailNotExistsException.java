package in.kr.main.exceptions;

public class EmailNotExistsException extends RuntimeException {
	public EmailNotExistsException(String message) {
		super(message);
	}
}
