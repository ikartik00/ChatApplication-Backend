package in.kr.main.io;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
	private String content;
	private String roomId;
	private String type;
	private String imageUrl;
	private String iv;
	private Map<String, String> encryptedKeys;
}
