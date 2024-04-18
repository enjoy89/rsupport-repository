package rsupport.jeondui.notice.domain.notice.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rsupport.jeondui.notice.common.aws.AmazonS3Service;
import rsupport.jeondui.notice.common.utils.FileUtil;
import rsupport.jeondui.notice.domain.attachment.entity.Attachment;
import rsupport.jeondui.notice.domain.attachment.repository.AttachmentRepository;
import rsupport.jeondui.notice.domain.member.entity.Member;
import rsupport.jeondui.notice.domain.member.service.MemberService;
import rsupport.jeondui.notice.domain.notice.controller.dto.request.NoticeRegisterRequest;
import rsupport.jeondui.notice.domain.notice.entity.Notice;
import rsupport.jeondui.notice.domain.notice.repository.NoticeRepository;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final AttachmentRepository attachmentRepository;
    private final AmazonS3Service amazonS3Service;
    private final MemberService memberService;

    /**
     * 공지사항 등록
     */
    public void registerNotice(Long memberId, NoticeRegisterRequest request) {
        Member member = memberService.findById(memberId); // 회원 조회
        Notice notice = noticeRepository.save(Notice.of(member, request)); // 공지사항 객체 생성 후 저장

        // 첨부파일이 존재할 때만 업로드
        if (isNotEmptyFile(request.getFiles())) {
            uploadAndSaveAttachments(notice, request.getFiles());
        }
    }

    /**
     * 첨부파일 업로드 후 저장
     */
    private void uploadAndSaveAttachments(Notice notice, List<MultipartFile> files) {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = amazonS3Service.uploadFile(file); // AWS S3 버킷에 첨부파일 업로드
                String fileType = FileUtil.getExtension(fileName);
                attachmentRepository.save(Attachment.of(notice, fileName, fileType)); // 첨부파일 객체 생성 후 저장
            }
        }
    }

    /**
     * 파일 존재 유무 판별
     */
    private boolean isNotEmptyFile(List<MultipartFile> files) {
        return !(files == null || files.isEmpty());
    }

    // TODO: 공지사항 수정
    // TODO: 공지사항 삭제
}
