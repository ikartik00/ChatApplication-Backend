package in.kr.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.kr.main.entity.Room;
import in.kr.main.entity.RoomMembers;
import in.kr.main.io.MemberResponseforUserRooms;

public interface RoomMemberRepository extends JpaRepository<RoomMembers, Long> {

	boolean existsByRoomIdAndEmail(String roomId, String email);

	boolean existsByRoomIdAndUserId(String roomId, String userId);

	boolean existsByEmailAndRoleAndRoomId(String email, String string, String roomId);

	boolean existsByEmailAndRole(String email, String string);
	
	@Query("SELECT r FROM rooms_tbl r where r.roomId in (SELECT rm.roomId from RoomMembers rm WHERE rm.userId = :userId)")
	List<Room> findByUserId(@Param("userId") String userId);

	Integer countByRoomId(String roomId);

	@Query("""
		    select new in.kr.main.io.MemberResponseforUserRooms(
		        u.name,
		        rm.email,
		        rm.roomId,
		        rm.userId
		    )
		    from RoomMembers rm
		    join UserEntity u on u.userId = rm.userId
		    where rm.role = 'USER' and rm.roomId in (
		        select r.roomId
		        from rooms_tbl r
		        where r.createdBy.userId = :userId
		    )
		""")
	List<MemberResponseforUserRooms> findAllUsersRooms(@Param("userId") String userId);
	boolean existsByUserIdAndRoomId(String userId, String roomId);
	void deleteByUserIdAndRoomId(String userId, String roomId);

	void deleteAllByRoomIdAndUserId(String roomId, String userId);

	void deleteAllByRoomId(String roomId);

	@Query("SELECT rm from RoomMembers rm WHERE rm.roomId = :roomId and rm.userId != :userId order By rm.joinedAt ASC")
	List<RoomMembers> findOtherMembers(String userId, String roomId);

	void deleteAllByUserId(String userId);


	List<RoomMembers> findAllByRoomId(String roomId);
}
