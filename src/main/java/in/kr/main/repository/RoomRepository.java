package in.kr.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.kr.main.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
	Optional<Room> findByRoomId(String roomId);
	boolean existsByRoomId(String roomId);
}
