package rsupport.jeondui.notice.common.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rsupport.jeondui.notice.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public class JwtTokenValidationResult {

    private final boolean valid;
    private final ErrorCode errorCode;

    public static JwtTokenValidationResult valid() {
        return new JwtTokenValidationResult(true, null);
    }

    public static JwtTokenValidationResult invalid(ErrorCode errorCode) {
        return new JwtTokenValidationResult(false, errorCode);
    }
}
