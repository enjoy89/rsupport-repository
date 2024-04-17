package rsupport.jeondui.notice.common.exception.custom;

import rsupport.jeondui.notice.common.exception.CustomException;
import rsupport.jeondui.notice.common.exception.ErrorCode;

public class NoticeException extends CustomException {
    public NoticeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
