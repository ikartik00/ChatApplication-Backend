package in.kr.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.kr.main.entity.UserKey;

public interface UserKeyRepository extends JpaRepository<UserKey, Long> {
	Optional<UserKey> findByUserId(String userId);

	List<UserKey> findByUserIdIn(List<String> userIds);
}
