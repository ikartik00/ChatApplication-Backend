package in.kr.main.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PrivateKeyResponse {
	private String privateKey;
	private String salt;
	private String iv;
}
