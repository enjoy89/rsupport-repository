package rsupport.jeondui.notice.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class Response<T> {

    private int code; // 상태 코드: 200, 400, 404, 500...
    private String message; // 응답 메세지

    @JsonInclude(Include.NON_NULL)
    private T data; // 응답 데이터 (존재할 경우만 포함)

    /**
     * 응답 성공 (응답 데이터가 없는 경우)
     */
    public static Response<Void> success(HttpStatus code, String message) {
        return new Response<>(code.value(), message, null);
    }

    /**
     * 응답 성공 (응답 데이터가 있는 경우)
     */
    public static <T> Response<T> success(HttpStatus code, String message, T data) {
        return new Response<>(code.value(), message, data);
    }

    /**
     * 응답 실패 (응답 데이터가 없는 경우)
     */
    public static Response<Void> fail(HttpStatus code, String message) {
        return new Response<>(code.value(), message, null);
    }

    /**
     * 응답 실패 (응답 데이터가 있는 경우)
     */
    public static <T> Response<T> fail(HttpStatus code, String message, T data) {
        return new Response<>(code.value(), message, data);
    }
}
