package rsupport.jeondui.notice.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import rsupport.jeondui.notice.common.response.Response;

@RestControllerAdvice
public class GlobalExceptionAdvice {

    // CustomException 예외 핸들러
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(HttpServletRequest request, CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(Response.fail(errorCode.getStatus(), errorCode.getMessage()));
    }

    // RuntimeException 예외 핸들러
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.fail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }

    // MethodArgumentNotValidException 예외 핸들러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response<List<String>>> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Response.fail(HttpStatus.BAD_REQUEST, "입력 데이터 형식 오류입니다.", errors));
    }

}
