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
public class AuthRequest {
	@NotNull(message = "Email is Required")
	@Email(message = "Please Enter the valid email")
	private String email;
	@NotNull(message = "Password is Required")
	private String password;
}
