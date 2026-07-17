package in.kr.main.io;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RoomMemberResponse {
	private String roomId;
	private String email;
	private String userId;
	private LocalDateTime joinedAt;
	private String role;
}
