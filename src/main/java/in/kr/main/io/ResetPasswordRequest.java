package in.kr.main.io;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResetPasswordRequest {
	@NotNull(message = "Token is Requuired")
	private String token;
	@NotNull(message = "Password is Required")
	@Size(min = 6, message = "Password should be of minimum 6 characters")
	private String password;
	private String publicKey;
	private String encryptedPrivateKey;
	private String iv;
	private String salt;
}
