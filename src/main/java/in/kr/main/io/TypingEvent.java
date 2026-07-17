package in.kr.main.io;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypingEvent {
	private String userId;
	private Boolean isTyping;
	private String roomId;
	private String name;
}
