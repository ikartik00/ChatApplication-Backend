package in.kr.main.service;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender javaMailSender;
	
	@Value("${spring.mail.properties.mail.smtp.from}")
	private String fromEmail;
	
	@Value("${brevo.api.key}")
    private String brevoApiKey;

	
	public void sendVerifyOtp(String email, String otp) {
//		SimpleMailMessage mailMessage = new SimpleMailMessage();
//		mailMessage.setFrom(fromEmail);
//		mailMessage.setTo(email);
//		mailMessage.setSubject("Verify Email OTP for ChitChat Application");
//		mailMessage.setText("Hello ," + email + "\n\nYour Otp for Verifying the account is " + otp + "\n User This Otp for Verifying the account \n\n Regards, \nChitChat Application");
//		javaMailSender.send(mailMessage);
		String url = "https://api.brevo.com/v3/smtp/email";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey); 

        String jsonBody = "{"
                + "\"sender\":{\"email\":\"" + fromEmail + "\"},"
                + "\"to\":[{\"email\":\"" + email + "\"}],"
                + "\"subject\":\"Verify Email OTP for ChitChat Application\","
                + "\"htmlContent\":\"<p>Hello," + email + "Your Otp for Verifying the account is : <b>" + otp + "<b><b>Regards, <b> ChitChat Application</b></p>\""
                + "}";

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println("Email Sent via API! " + response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Unable to Send Email: " + e.getMessage());
        }
    }
	
	public void sendResetLink(String email, String token) {
//		SimpleMailMessage mailMessage = new SimpleMailMessage();
//		mailMessage.setFrom(fromEmail);
//		mailMessage.setTo(email);
//		mailMessage.setSubject("Reset Password Link");
//		mailMessage.setText("Hello ," + email + "\n\nWe received a request to reset your password. \n\n Click the or link below to create a new password: \n\n " + "http://localhost:5173/reset-password?token="+token+"\n\n This Link Will Expire in 5 Minutes \n\n\nRegards,\nChitChat Application");
//		javaMailSender.send(mailMessage);
		
		String url = "https://api.brevo.com/v3/smtp/email";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey); 

        String jsonBody = "{"
                + "\"sender\":{\"email\":\"" + fromEmail + "\"},"
                + "\"to\":[{\"email\":\"" + email + "\"}],"
                + "\"subject\":\"Reset Password Link\","
                + "\"htmlContent\":\"<p>Hello," + email + "YWe received a request to reset your password. <b><b> Click the or link below to create a new password : <b> http://localhost:5173/reset-password?token="+token + "<b> This Link Will Expire in 5 Minutes <b><b>Regards, <b> ChitChat Application</b></p>\""
                + "}";

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            System.out.println("Reset Otp Sent ! " + response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Unable to Send Otp: " + e.getMessage());
        }
	}
}
