package in.kr.main.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberResponseforUserRooms {
	private String name;
	private String email;
	private String roomId;
	private String userId;
}
