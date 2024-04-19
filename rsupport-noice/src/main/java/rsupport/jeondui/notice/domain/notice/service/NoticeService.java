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
import rsupport.jeondui.notice.common.exception.ErrorCode;
import rsupport.jeondui.notice.common.exception.custom.NoticeException;
import rsupport.jeondui.notice.domain.attachment.service.AttachmentService;
import rsupport.jeondui.notice.domain.member.entity.Member;
import rsupport.jeondui.notice.domain.member.service.MemberService;
import rsupport.jeondui.notice.domain.notice.controller.dto.request.NoticeModifyRequest;
import rsupport.jeondui.notice.domain.notice.controller.dto.request.NoticeRegisterRequest;
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
    private final AttachmentService attachmentService;
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
        return NoticeDetailResponse.of(notice, attachmentService.getAttachmentResponse(notice));
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
        attachmentService.deleteAttachments(notice.getAttachmentIds()); // 업로드 했던 첨부파일들을 함께 삭제
        noticeRepository.delete(notice);
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
            attachmentService.deleteAttachments(deleteAttachmentIds);
        }

        if (isNotEmpty(files)) {
            attachmentService.uploadAndSaveAttachments(notice, files);
        }
    }

    /**
     * 리스트 값 존재 유무 판별
     */
    private <T> boolean isNotEmpty(Collection<T> collection) {
        return collection != null && !collection.isEmpty();
    }

}
