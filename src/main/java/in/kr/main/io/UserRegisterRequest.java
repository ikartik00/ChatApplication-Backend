package in.kr.main.io;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterRequest {
	@NotNull(message = "Name is required")
	private String name;
	@NotNull(message = "Email is Required")
	@Email(message = "Please Enter valid email")
	private String email;
	@NotNull(message = "password is required")
	@Size(min = 6, message = "Password should be of 6 characters")
	private String password;
	private String publicKey;
	private String encryptedPrivateKey;
	private String iv;
	private String salt;
}
