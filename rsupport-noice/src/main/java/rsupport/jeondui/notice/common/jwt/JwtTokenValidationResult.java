package rsupport.jeondui.notice.common.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rsupport.jeondui.notice.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public class JwtTokenValidationResult {

    private final boolean valid; // 토큰 유효성
    private final ErrorCode errorCode; // 토큰이 유효하지 않은 경우의 에러 코드

    public static JwtTokenValidationResult valid() {
        return new JwtTokenValidationResult(true, null);
    }

    public static JwtTokenValidationResult invalid(ErrorCode errorCode) {
        return new JwtTokenValidationResult(false, errorCode);
    }
}
