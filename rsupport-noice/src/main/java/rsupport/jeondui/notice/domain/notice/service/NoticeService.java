package rsupport.jeondui.notice.domain.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rsupport.jeondui.notice.common.aws.AmazonS3Service;
import rsupport.jeondui.notice.common.utils.FileUtil;
import rsupport.jeondui.notice.domain.attachment.entity.Attachment;
import rsupport.jeondui.notice.domain.attachment.repository.AttachmentRepository;
import rsupport.jeondui.notice.domain.notice.controller.dto.request.NoticeRegisterRequest;
import rsupport.jeondui.notice.domain.notice.entity.Notice;
import rsupport.jeondui.notice.domain.notice.repository.NoticeRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final AttachmentRepository attachmentRepository;
    private final AmazonS3Service amazonS3Service;

    /**
     * 공지사항 등록
     */
    public void registerNotice(NoticeRegisterRequest request) {
        Notice notice = noticeRepository.save(Notice.of(request)); // 공지사항 객체 생성 후 저장

        for (MultipartFile file : request.getFiles()) {
            // AWS S3 버킷에 첨부파일 업로드
            String fileName = amazonS3Service.uploadFile(file);
            String fileType = FileUtil.getExtension(fileName);

            attachmentRepository.save(Attachment.of(notice, fileName, fileType)); // 첨부파일 객체 생성 후 저장
        }

    }

    // TODO: 공지사항 수정
    // TODO: 공지사항 삭제
    // TODO: 공지사항 조회
}
