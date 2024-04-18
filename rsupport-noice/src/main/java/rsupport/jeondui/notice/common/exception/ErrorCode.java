package rsupport.jeondui.notice.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    PASSWORD_MISMATCH("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_MEMBER("회원이 존재하지 않습니다", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_LOGIN("회원 인증 실패", HttpStatus.UNAUTHORIZED),

    MEMBER_ALREADY_EXISTS("이미 존재하는 회원입니다.", HttpStatus.CONFLICT),
    NICKNAME_ALREADY_EXISTS("이미 존재하는 닉네임입니다.", HttpStatus.CONFLICT),

    NOT_FOUND_NOTICE("등록된 공지사항을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),

    INTERNAL_SERVER_ERROR("서버 에러 발생", HttpStatus.INTERNAL_SERVER_ERROR),
    AWS_S3_FILE_UPLOAD_FAIL("AWS S3 파일 업로드 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    AWS_S3_FILE_DELETE_FAIL("AWS S3 파일 삭제 실패", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus status;
}
