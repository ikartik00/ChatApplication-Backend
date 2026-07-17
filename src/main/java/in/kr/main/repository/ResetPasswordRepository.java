package in.kr.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.kr.main.entity.ResetPassword;

public interface ResetPasswordRepository extends JpaRepository<ResetPassword, Long> {

	Optional<ResetPassword> findByEmail(String email);

	Optional<ResetPassword> findByToken(String token);

}
