package rsupport.jeondui.notice.domain.notice.service;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rsupport.jeondui.notice.common.aws.AmazonS3Service;
import rsupport.jeondui.notice.common.exception.ErrorCode;
import rsupport.jeondui.notice.common.exception.custom.AttachmentException;
import rsupport.jeondui.notice.common.exception.custom.NoticeException;
import rsupport.jeondui.notice.common.utils.FileUtil;
import rsupport.jeondui.notice.domain.attachment.entity.Attachment;
import rsupport.jeondui.notice.domain.attachment.repository.AttachmentRepository;
import rsupport.jeondui.notice.domain.member.entity.Member;
import rsupport.jeondui.notice.domain.member.service.MemberService;
import rsupport.jeondui.notice.domain.notice.controller.dto.request.NoticeModifyRequest;
import rsupport.jeondui.notice.domain.notice.controller.dto.request.NoticeRegisterRequest;
import rsupport.jeondui.notice.domain.notice.controller.dto.response.AttachmentResponse;
import rsupport.jeondui.notice.domain.notice.controller.dto.response.NoticeDetailResponse;
import rsupport.jeondui.notice.domain.notice.controller.dto.response.PagedNoticeResponse;
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

        handleAttachments(notice, request.getFiles(), null);
    }

    /**
     * 공지사항 전체 조회
     */
    public PagedNoticeResponse findAll(Pageable pageable) {
        Page<Notice> notices = noticeRepository.findAll(pageable);
        return PagedNoticeResponse.of(notices);
    }

    /**
     * 공지사항 상세 조회
     */
    public NoticeDetailResponse findById(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoticeException(ErrorCode.NOT_FOUND_NOTICE));

        notice.setViewCount(notice.getViewCount() + 1); // 조회수 증가
        return NoticeDetailResponse.of(notice, getAttachmentResponse(notice));
    }

    /**
     * 공지사항 수정
     */
    public void modifyNotice(Long noticeId, Long memberId, NoticeModifyRequest request) {
        Member member = memberService.findById(memberId); // 회원 조회
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoticeException(ErrorCode.NOT_FOUND_NOTICE)); // 공지사항 조회

        validationMember(notice, member);

        // 내용 수정 후 저장
        notice.modify(request);
        noticeRepository.save(notice);

        handleAttachments(notice, request.getFiles(), request.getDeleteAttachmentIds());
    }

    /**
     * 공지사항 삭제
     */
    public void deleteNotice(Long noticeId, Long memberId) {
        Member member = memberService.findById(memberId); // 회원 조회
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoticeException(ErrorCode.NOT_FOUND_NOTICE)); // 공지사항 조회

        validationMember(notice, member);
        noticeRepository.delete(notice);
    }

    /**
     * 첨부파일 목록 반환값으로 변환
     */
    private List<AttachmentResponse> getAttachmentResponse(Notice notice) {
        return notice.getAttachments().stream()
                .map(attachment -> AttachmentResponse.of(attachment,
                        amazonS3Service.getFileUrl(attachment.getFileName())))
                .toList();
    }

    /**
     * 공지사항을 등록한 작성자인지 검증
     */
    private void validationMember(Notice notice, Member member) {
        if (!notice.getMember().getId().equals(member.getId())) {
            throw new NoticeException(ErrorCode.UNAUTHORIZED_LOGIN);
        }
    }

    /**
     * 첨부파일 등록, 삭제 관련 핸들러
     */
    private void handleAttachments(Notice notice, List<MultipartFile> files, List<Long> deleteAttachmentIds) {
        if (isNotEmpty(deleteAttachmentIds)) {
            deleteAttachments(deleteAttachmentIds);
        }

        if (isNotEmpty(files)) {
            uploadAndSaveAttachments(notice, files);
        }
    }

    /**
     * 첨부파일 삭제
     */
    private void deleteAttachments(List<Long> attachmentIds) {
        attachmentIds.forEach(attachmentId -> {
            Attachment attachment = attachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new AttachmentException(ErrorCode.NOT_FOUND_ATTACHMENT));
            amazonS3Service.deleteFile(attachment.getFileName());
            attachmentRepository.delete(attachment);
        });
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
     * 리스트 값 존재 유무 판별
     */
    private <T> boolean isNotEmpty(Collection<T> collection) {
        return collection != null && !collection.isEmpty();
    }

}
