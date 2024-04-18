package rsupport.jeondui.notice.domain.notice.controller.dto.response;

import lombok.Builder;
import lombok.Getter;
import rsupport.jeondui.notice.domain.attachment.entity.Attachment;

@Getter
public class AttachmentResponse {

    private Long id;             // 첨부파일 고유 ID
    private String fileName;     // 첨부파일 이름
    private String fileType;     // 첨부파일 타입 (파일 확장자)
    private String fileUrl;      // 첨부파일 URL

    @Builder
    private AttachmentResponse(Long id, String fileName, String fileType, String fileUrl) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileUrl = fileUrl;
    }

    public static AttachmentResponse of(Attachment attachment, String fileUrl) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .fileName(attachment.getFileName())
                .fileType(attachment.getFileType())
                .fileUrl(fileUrl)
                .build();
    }
}
