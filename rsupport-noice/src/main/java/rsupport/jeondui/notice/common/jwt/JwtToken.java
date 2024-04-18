package rsupport.jeondui.notice.common.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class JwtToken {

    private String grantType;
    private String accessToken;
}
