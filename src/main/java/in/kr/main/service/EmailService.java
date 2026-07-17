package in.kr.main.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender javaMailSender;
	
	@Value("${spring.mail.properties.mail.smtp.from}")
	private String fromEmail;
	
	public void sendVerifyOtp(String email, String otp) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(fromEmail);
		mailMessage.setTo(email);
		mailMessage.setSubject("Verify Email OTP for ChitChat Application");
		mailMessage.setText("Hello ," + email + "\n\nYour Otp for Verifying the account is " + otp + "\n User This Otp for Verifying the account \n\n Regards, \nChitChat Application");
		javaMailSender.send(mailMessage);
	}
	
	public void sendResetLink(String email, String token) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(fromEmail);
		mailMessage.setTo(email);
		mailMessage.setSubject("Reset Password Link");
		mailMessage.setText("Hello ," + email + "\n\nWe received a request to reset your password. \n\n Click the or link below to create a new password: \n\n " + "http://localhost:5173/reset-password?token="+token+"\n\n This Link Will Expire in 5 Minutes \n\n\nRegards,\nChitChat Application");
		javaMailSender.send(mailMessage);
	}
}
