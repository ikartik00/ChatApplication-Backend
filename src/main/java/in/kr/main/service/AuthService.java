package in.kr.main.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import in.kr.main.entity.EmailVerification;
import in.kr.main.entity.ResetPassword;
import in.kr.main.entity.UserEntity;
import in.kr.main.entity.UserKey;
import in.kr.main.exceptions.EmailAlreadyExistsException;
import in.kr.main.exceptions.EmailNotExistsException;
import in.kr.main.exceptions.InvalidEmailException;
import in.kr.main.exceptions.InvalidOtpException;
import in.kr.main.exceptions.InvalidTokenException;
import in.kr.main.exceptions.OtpOrTokenExpiredException;
import in.kr.main.io.ResetPasswordRequest;
import in.kr.main.repository.EmailVerificationRepository;
import in.kr.main.repository.ResetPasswordRepository;
import in.kr.main.repository.UserKeyRepository;
import in.kr.main.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final EmailVerificationRepository verificationRepository;
	private final EmailService emailService;
	private final ResetPasswordRepository passwordRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserKeyRepository userKeyRepository;

	public void sendVerifyOtp(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new EmailAlreadyExistsException("This Email is Already Exists");
		}
		Optional<EmailVerification> emailVerification2 = verificationRepository.findByEmail(email);
		if (emailVerification2.isPresent()) {
			verificationRepository.delete(emailVerification2.get());
		}
		String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
		long expiryTime = System.currentTimeMillis() + (1000 * 60 * 60 * 24);
		EmailVerification emailVerification = new EmailVerification();
		emailVerification.setEmail(email);
		emailVerification.setOtp(otp);
		emailVerification.setExpiry(expiryTime);
		emailVerification.setVerified(false);
		verificationRepository.save(emailVerification);

		try {
			emailService.sendVerifyOtp(email, otp);
		} catch (Exception e) {
			throw new RuntimeException("Unable to Send Email");
		}
	}

	public void verifyOtp(Map<String, String> request) {
		String email = request.get("email");
		String otp = request.get("otp");
		if (email == null || otp == null) {
			throw new RuntimeException("Please Enter the Otp or Email");
		}
		EmailVerification emailVerification = verificationRepository.findByEmail(email)
				.orElseThrow(() -> new InvalidEmailException("Invalid Email"));
		String DBOtp = emailVerification.getOtp();
		Long expiry = emailVerification.getExpiry();
		if (!otp.equals(DBOtp)) {
			throw new InvalidOtpException("Invalid Otp");
		}
		if (System.currentTimeMillis() > expiry) {
			throw new OtpOrTokenExpiredException("OTP is Expired");
		}
		emailVerification.setVerified(true);
		verificationRepository.save(emailVerification);
	}

	public void sendResetLink(String email) {
		if (!userRepository.existsByEmail(email)) {
			throw new EmailNotExistsException("This Email is not registered with us!");
		}
		Optional<ResetPassword> resetPasswordd = passwordRepository.findByEmail(email);
		if(resetPasswordd.isPresent()) {
			passwordRepository.delete(resetPasswordd.get());
		}
		long expiryTime = System.currentTimeMillis() + (1000 * 60 * 5);
		String token = UUID.randomUUID().toString();

		ResetPassword resetPassword = new ResetPassword();
		resetPassword.setEmail(email);
		resetPassword.setExpiry(expiryTime);
		resetPassword.setToken(token);
		passwordRepository.save(resetPassword);
		emailService.sendResetLink(email, token);
	}
	
	@Transactional
	public void resetPassword(ResetPasswordRequest request) {
		if(request.getPassword().trim().length()<6) {
			throw new IllegalArgumentException("Password must contain atleast 6 characters");
		}
		ResetPassword resetPassword = passwordRepository.findByToken(request.getToken()).orElseThrow(()-> new InvalidTokenException("Invalid Token"));
		if(System.currentTimeMillis() > resetPassword.getExpiry()) {
			throw new OtpOrTokenExpiredException("The Link is Expired");
		}
		UserEntity user = userRepository.findByEmail(resetPassword.getEmail()).orElseThrow(()-> new UsernameNotFoundException("User Not found with this email " + resetPassword.getEmail()));
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		
		UserKey userKey = userKeyRepository.findByUserId(user.getUserId()).orElseThrow(()-> new UsernameNotFoundException("User Not exists with this id"));
		userKey.setEncryptedPrivateKey(request.getEncryptedPrivateKey());
		userKey.setIv(request.getIv());
		userKey.setSalt(request.getSalt());
		userKey.setPublicKey(request.getPublicKey());
		userKeyRepository.save(userKey);
		userRepository.save(user);
	}
}
