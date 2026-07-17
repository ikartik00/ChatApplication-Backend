package in.kr.main.io;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ChangePasswordRequest {
	@NotNull
	@Pattern(regexp = "^\\S+$", message = "Old password cannot contain spaces")
	private String oldPassword;
	@NotNull
	@Pattern(regexp = "^\\S+$", message = "New password cannot contain spaces")
	@Size(min = 6, message = "New Password must be of 6 characters")
	private String newPassword;
	private String encryptedKey;
	private String iv;
	private String salt;
}
