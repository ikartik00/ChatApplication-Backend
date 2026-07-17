package in.kr.main.exceptions;

public class OtpOrTokenExpiredException extends RuntimeException {
	public OtpOrTokenExpiredException(String message) {
		super(message);
	}
}	
