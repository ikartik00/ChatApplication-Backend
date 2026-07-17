package in.kr.main.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "email_verification_tbl")
public class EmailVerification {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	private String email;
	private String otp;
	private Boolean verified;
	private Long expiry;
	@CreationTimestamp
	private LocalDateTime createdAt;
}
