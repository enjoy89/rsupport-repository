package rsupport.jeondui.notice.common.exception.custom;

import rsupport.jeondui.notice.common.exception.CustomException;
import rsupport.jeondui.notice.common.exception.ErrorCode;

public class AmazonS3Exception extends CustomException {
    public AmazonS3Exception(ErrorCode errorCode) {
        super(errorCode);
    }
}
