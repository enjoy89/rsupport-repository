package rsupport.jeondui.notice.common.exception.custom;

import rsupport.jeondui.notice.common.exception.CustomException;
import rsupport.jeondui.notice.common.exception.ErrorCode;

public class MemberException extends CustomException {
    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
