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
public class RoomResponse {
	private String roomId;
	private String adminName;
	private String userId;
	private LocalDateTime createdAt;
	private Integer members;
}
