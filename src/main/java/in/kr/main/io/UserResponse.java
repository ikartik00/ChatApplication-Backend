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
public class UserResponse {
	private String userId;
	private String name;
	private String email;
	private String imgUrl;
	private LocalDateTime createdAt;
}
