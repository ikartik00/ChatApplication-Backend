package in.kr.main.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reset-password-tbl")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResetPassword {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Email(message = "Please Enter Valid Email")
	private String email;
	@NotNull(message = "Token is Required")
	private String token;
	@NotNull(message = "Expiry is Required")
	private Long expiry;
}
