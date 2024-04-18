package rsupport.jeondui.notice.common.exception.custom;

import rsupport.jeondui.notice.common.exception.CustomException;
import rsupport.jeondui.notice.common.exception.ErrorCode;

public class AttachmentException extends CustomException {
    public AttachmentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
