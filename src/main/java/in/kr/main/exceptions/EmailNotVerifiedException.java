package in.kr.main.exceptions;

public class EmailNotVerifiedException extends RuntimeException{
	public EmailNotVerifiedException(String message) {
		super(message);
	}
}
