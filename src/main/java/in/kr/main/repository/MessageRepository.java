package in.kr.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.kr.main.entity.Messages;
import in.kr.main.entity.UserEntity;

@Repository
public interface MessageRepository extends JpaRepository<Messages, Long> {


	@Modifying
	@Query("UPDATE Messages m SET m.user = null WHERE m.user.userId = :userId")
	void setUserToNull(String userId);
	
}
