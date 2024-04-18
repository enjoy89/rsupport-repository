package rsupport.jeondui.notice.domain.member.controller.dto.response;

import lombok.Builder;
import lombok.Getter;
import rsupport.jeondui.notice.common.jwt.JwtToken;

@Getter
public class MemberLoginResponse {

    private final String accessToken;

    @Builder
    private MemberLoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public static MemberLoginResponse of(JwtToken jwtToken) {
        return MemberLoginResponse.builder()
                .accessToken(jwtToken.getAccessToken())
                .build();
    }
}
