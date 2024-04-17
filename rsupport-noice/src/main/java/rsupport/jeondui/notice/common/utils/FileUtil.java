package rsupport.jeondui.notice.common.utils;

import java.util.Optional;

public class FileUtil {

    /**
     * 파일 확장자를 추출하여 반환하는 메서드
     */
    public static String getExtension(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".") + 1))
                .orElse("");
    }
}
