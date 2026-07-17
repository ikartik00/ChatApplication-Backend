package in.kr.main.io;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RoomMemberRequest {
	@NotNull(message = "Email is Required")
	@Email(message = "Please Enter Valid Email")
	private String email;
	private String roomId;
}
