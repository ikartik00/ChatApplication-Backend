package in.kr.main.io;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {
	private String content;
	private LocalDateTime sentAt;
	private String senderUserId;
	private String senderName;
	private String senderImageUrl;
	private String type;
	private String iv;
	private String myEncryptedKey;
	private String imageUrl;
}
