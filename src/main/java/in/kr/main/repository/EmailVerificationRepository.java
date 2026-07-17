package in.kr.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.kr.main.entity.EmailVerification;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String> {

	Optional<EmailVerification> findByEmail(String string);
	boolean existsByEmail(String email);

}
