package in.kr.main.entity;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "user_key_tbl")
public class UserKey {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(columnDefinition = "LONGTEXT")
	private String publicKey;
	private LocalDateTime generatedAt;
	private String userId;
	@Column(columnDefinition = "LONGTEXT")
	private String encryptedPrivateKey;
	@Column(columnDefinition = "LONGTEXT")
	private String iv;
	@Column(columnDefinition = "LONGTEXT")
	private String salt;
}
