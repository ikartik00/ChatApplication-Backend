package in.kr.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.kr.main.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	boolean existsByEmail(String email);

	Optional<UserEntity> findByEmail(String email);
	
	Optional<UserEntity> findByUserId(String userId);

	List<UserEntity> findByEmailContainingIgnoreCase(String name);

	List<UserEntity> findByUserIdIn(List<String> memberIds);

}
