package rsupport.jeondui.notice.domain.attachment.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rsupport.jeondui.notice.common.aws.AmazonS3Service;
import rsupport.jeondui.notice.common.exception.ErrorCode;
import rsupport.jeondui.notice.common.exception.custom.AttachmentException;
import rsupport.jeondui.notice.common.utils.FileUtil;
import rsupport.jeondui.notice.domain.attachment.entity.Attachment;
import rsupport.jeondui.notice.domain.attachment.repository.AttachmentRepository;
import rsupport.jeondui.notice.domain.notice.controller.dto.response.AttachmentResponse;
import rsupport.jeondui.notice.domain.notice.entity.Notice;

@Service
@RequiredArgsConstructor
@EnableAsync
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final AmazonS3Service amazonS3Service;

    /**
     * 첨부파일 업로드 후 저장
     */
    @Async
    public void uploadAndSaveAttachments(Notice notice, List<MultipartFile> files) {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = amazonS3Service.uploadFile(file); // AWS S3 버킷에 첨부파일 업로드
                String fileType = FileUtil.getExtension(fileName);
                attachmentRepository.save(Attachment.of(notice, fileName, fileType)); // 첨부파일 객체 생성 후 저장
            }
        }
    }

    /**
     * 첨부파일 삭제
     */
    @Async
    public void deleteAttachments(List<Long> attachmentIds) {
        attachmentIds.forEach(attachmentId -> {
            Attachment attachment = attachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new AttachmentException(ErrorCode.NOT_FOUND_ATTACHMENT));
            amazonS3Service.deleteFile(attachment.getFileName());   // AWS S3 버킷에서 파일 삭제
            attachmentRepository.delete(attachment);
        });
    }

    /**
     * 첨부파일 목록 반환값으로 변환
     */
    public List<AttachmentResponse> getAttachmentResponse(Notice notice) {
        return notice.getAttachments().stream()
                .map(attachment -> AttachmentResponse.of(attachment,
                        amazonS3Service.getFileUrl(attachment.getFileName())))
                .toList();
    }

}
