package rsupport.jeondui.notice.domain.attachment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import rsupport.jeondui.notice.common.aws.AmazonS3Service;
import rsupport.jeondui.notice.domain.attachment.entity.Attachment;
import rsupport.jeondui.notice.domain.attachment.repository.AttachmentRepository;
import rsupport.jeondui.notice.domain.member.entity.Member;
import rsupport.jeondui.notice.domain.member.entity.Role;
import rsupport.jeondui.notice.domain.notice.entity.Notice;

@SpringBootTest
class AttachmentServiceTest {

    @MockBean
    private AttachmentRepository attachmentRepository;
    @MockBean
    private AmazonS3Service amazonS3Service;
    @Autowired
    private AttachmentService attachmentService;

    private Member member;
    private Notice notice;
    private MockMultipartFile mockFile;
    private Attachment attachment;

    @BeforeEach
    void setUp() {
        member = createMember();
        notice = createNotice();
        mockFile = new MockMultipartFile("file", "uploadedFileName.txt", "text/plain", "Some contents".getBytes());
        attachment = Attachment.of(notice, mockFile.getOriginalFilename(), mockFile.getContentType());
        ReflectionTestUtils.setField(attachment, "id", 1L);
        when(attachmentRepository.findById(1L)).thenReturn(Optional.ofNullable(attachment));
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(attachmentRepository, amazonS3Service);
    }

    @Test
    @DisplayName("첨부파일 업로드 테스트")
    void 첨부파일_업로드() throws InterruptedException {
        // given
        CountDownLatch latch = new CountDownLatch(1); // 카운트다운 래치 생성

        when(amazonS3Service.uploadFile(any(MultipartFile.class))).thenReturn("uploadedFileName.txt");
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(attachment);

        // 비동기 작업이 완료될 때 카운트다운 래치를 해제하는 콜백을 설정
        doAnswer(invocation -> {
            latch.countDown(); // 카운트다운 래치를 1 감소시킴
            return null;
        }).when(attachmentRepository).save(any(Attachment.class));

        // when
        attachmentService.uploadAndSaveAttachments(notice, Collections.singletonList(mockFile));
        latch.await(); // 대기하도록 설정

        // then
        verify(attachmentRepository).save(any(Attachment.class));
    }

    @Test
    @DisplayName("첨부파일 삭제 테스트")
    void 첨부파일_삭제() throws InterruptedException {
        // given
        CountDownLatch latch = new CountDownLatch(1); // 카운트다운 래치 생성

        doAnswer(invocation -> {
            latch.countDown(); // 카운트다운 래치를 1 감소시킴
            return null;
        }).when(attachmentRepository).delete(any(Attachment.class));

        // when
        attachmentService.deleteAttachments(Collections.singletonList(attachment.getId()));
        latch.await();

        // then
        verify(amazonS3Service, times(1)).deleteFile(attachment.getFileName());
        verify(attachmentRepository, times(1)).delete(attachment);
    }


    private Member createMember() {
        member = Member.builder()
                .email("user@example.com")
                .nickname("nickname")
                .password("password")
                .role(Role.USER)
                .build();

        ReflectionTestUtils.setField(member, "id", 1L);
        return member;
    }

    private Notice createNotice() {
        notice = Notice.builder()
                .member(member)
                .title("공지사항 제목")
                .content("공지사항 내용")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(7))
                .viewCount(0L)
                .build();

        ReflectionTestUtils.setField(notice, "id", 1L);
        return notice;
    }
}