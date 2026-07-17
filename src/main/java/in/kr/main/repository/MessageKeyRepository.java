package in.kr.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.kr.main.entity.MessageKey;

public interface MessageKeyRepository extends JpaRepository<MessageKey, Long> {
	
}
