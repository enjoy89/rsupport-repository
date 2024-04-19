package rsupport.jeondui.notice.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import rsupport.jeondui.notice.common.exception.ErrorCode;
import rsupport.jeondui.notice.common.response.Response;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        // 토큰이 유효한 경우에만 인증 정보를 설정
        if (token != null) {
            JwtTokenValidationResult validationResult = jwtTokenProvider.validateToken(token);
            if (validationResult.isValid()) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("토큰이 유효합니다.");
            } else {
                sendErrorResponse(response, validationResult.getErrorCode());
                return; // 유효하지 않은 경우, 처리를 중단
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청에서 토큰을 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 유효하지 않은 토큰에 대한 에러 반환
     */
    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        Response<Void> errorResponse = Response.fail(errorCode.getStatus(), errorCode.getMessage());
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getStatus().value()); // ErrorCode에 따른 적절한 HTTP 상태 코드 설정
        response.getWriter().print(new ObjectMapper().writeValueAsString(errorResponse)); // 재사용 가능한 ObjectMapper 인스턴스 사용
    }
}
