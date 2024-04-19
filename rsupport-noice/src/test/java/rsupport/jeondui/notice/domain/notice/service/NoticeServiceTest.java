package rsupport.jeondui.notice.domain.notice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import rsupport.jeondui.notice.common.exception.ErrorCode;
import rsupport.jeondui.notice.common.exception.custom.NoticeException;
import rsupport.jeondui.notice.domain.member.entity.Member;
import rsupport.jeondui.notice.domain.member.entity.Role;
import rsupport.jeondui.notice.domain.member.repository.MemberRepository;
import rsupport.jeondui.notice.domain.member.service.MemberService;
import rsupport.jeondui.notice.domain.notice.controller.dto.request.NoticeModifyRequest;
import rsupport.jeondui.notice.domain.notice.controller.dto.request.NoticeRegisterRequest;
import rsupport.jeondui.notice.domain.notice.entity.Notice;
import rsupport.jeondui.notice.domain.notice.repository.NoticeRepository;

@SpringBootTest
class NoticeServiceTest {

    @Mock
    private MemberService memberService;
    @Mock
    private NoticeRepository noticeRepository;
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private NoticeService noticeService;

    private Member member;
    private Notice notice;
    private NoticeRegisterRequest registerRequest;
    private final Long memberId = 1L;
    private final Long noticeId = 1L;

    @BeforeEach
    void setUp() {
        member = createMember();
        notice = createNotice();

        // Reflection을 사용하여 id 설정
        ReflectionTestUtils.setField(notice, "id", noticeId);
        ReflectionTestUtils.setField(member, "id", memberId);

        registerRequest = new NoticeRegisterRequest("공지사항 제목", "공지사항 내용", LocalDateTime.now(),
                LocalDateTime.now().plusDays(7), null); // 첨부파일은 null로 설정

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberService.findById(memberId)).thenReturn(member);
        when(noticeRepository.save(Mockito.any())).thenReturn(notice);
        when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(notice));
    }

    @Test
    @DisplayName("공지사항 등록 성공 테스트")
    void 공지사항_등록() {
        // when
        noticeService.registerNotice(memberId, registerRequest);

        // then
        verify(noticeRepository).save(Mockito.any());
    }

    @Test
    @DisplayName("공지사항 등록 실패 테스트 - 사용자 정보가 없는 경우")
    void 공지사항_등록_실패() {
        // given
        Long invalidMemberId = null;

        // when
        noticeService.registerNotice(invalidMemberId, registerRequest); // 사용자 정보가 없는 경우

        // then
        doThrow(new RuntimeException()).when(noticeRepository).save(Mockito.any()); // 서버 에러 발생
    }

    @Test
    @DisplayName("공지사항 수정 성공 테스트")
    void 공지사항_수정() {
        // given
        NoticeModifyRequest modifyRequest = new NoticeModifyRequest("수정 공지사항 제목", "수정 공지사항 내용", LocalDateTime.now(),
                LocalDateTime.now().plusDays(7), null, null);

        // when
        noticeService.modifyNotice(noticeId, memberId, modifyRequest);

        // then
        // Notice 객체의 내용이 수정 요청에 맞게 변경되었는지 확인
        assertThat(notice.getTitle()).isEqualTo(modifyRequest.getTitle());
        assertThat(notice.getContent()).isEqualTo(modifyRequest.getContent());
    }

    @Test
    @DisplayName("공지사항 수정 실패 테스트 - 해당 공지사항이 존재하지 않은 경우")
    void 공지사항_수정_실패_1() {
        // given
        NoticeModifyRequest modifyRequest = new NoticeModifyRequest("수정 공지사항 제목", "수정 공지사항 내용", LocalDateTime.now(),
                LocalDateTime.now().plusDays(7), null, null);

        Long invalidNoticeId = 2L; // id가 2인 공지사항 객체는 현재 존재하지 않음

        // when & then
        // 공지사항을 찾지 못하는 예외가 발생하는지 테스트
        Exception exception = assertThrows(
                NoticeException.class, () -> noticeService.modifyNotice(invalidNoticeId, memberId, modifyRequest));
        assertEquals(exception.getMessage(), ErrorCode.NOT_FOUND_NOTICE.getMessage());
    }

    @Test
    @DisplayName("공지사항 수정 실패 테스트 - 해당 회원의 권한이 존재하지 않은 경우")
    void 공지사항_수정_실패_2() {
        // given
        NoticeModifyRequest modifyRequest = new NoticeModifyRequest("수정 공지사항 제목", "수정 공지사항 내용", LocalDateTime.now(),
                LocalDateTime.now().plusDays(7), null, null);

        // 새로운 회원 생성
        Member otherMember = createMember();
        Long otherMemberId = 2L; // 새로운 회원 아이디

        ReflectionTestUtils.setField(otherMember, "id", otherMemberId);  // id가 2인 회원은 해당 공지사항 글의 수정 권한이 없음
        when(memberRepository.findById(otherMemberId)).thenReturn(Optional.of(otherMember));
        when(memberService.findById(otherMemberId)).thenReturn(otherMember);

        noticeService.registerNotice(memberId, registerRequest);

        // when & then
        // 회원 인증 실패 예외가 발생하는지 테스트
        Exception exception = assertThrows(
                NoticeException.class, () -> noticeService.modifyNotice(noticeId, otherMemberId, modifyRequest));
        assertEquals(exception.getMessage(), ErrorCode.UNAUTHORIZED_LOGIN.getMessage());
    }

    @Test
    @DisplayName("아이디 값으로 공지사항 조회 테스트")
    void 아이디_값을_통한_공지사항_조회() {
        // when
        noticeService.findById(noticeId);

        // then
        // noticeRepository의 findById 메소드 호출 확인
        verify(noticeRepository).findById(noticeId);
    }

    @Test
    @DisplayName("공지사항 삭제 성공 테스트")
    void 공지사항_삭제() {
        // when
        noticeService.deleteNotice(noticeId, memberId);

        // then
        verify(noticeRepository).delete(notice);
    }

    @Test
    @DisplayName("공지사항 삭제 실패 테스트 - 해당 공지사항이 존재하지 않은 경우")
    void 공지사항_삭제_실패_1() {
        // given
        Long invalidNoticeId = 2L; // id가 2인 공지사항 객체는 현재 존재하지 않음

        // when & then
        // 공지사항을 찾지 못하는 예외가 발생하는지 테스트
        Exception exception = assertThrows(
                NoticeException.class, () -> noticeService.deleteNotice(invalidNoticeId, memberId));
        assertEquals(exception.getMessage(), ErrorCode.NOT_FOUND_NOTICE.getMessage());
    }

    @Test
    @DisplayName("공지사항 삭제 실패 테스트 - 해당 회원의 권한이 존재하지 않은 경우")
    void 공지사항_삭제_실패_2() {
        // given
        // 새로운 회원 생성
        Member otherMember = createMember();
        Long otherMemberId = 2L; // 새로운 회원 아이디

        ReflectionTestUtils.setField(otherMember, "id", otherMemberId);  // id가 2인 회원은 해당 공지사항 글의 삭제 권한이 없음
        when(memberRepository.findById(otherMemberId)).thenReturn(Optional.of(otherMember));
        when(memberService.findById(otherMemberId)).thenReturn(otherMember);

        noticeService.registerNotice(memberId, registerRequest);

        // when & then
        // 회원 인증 실패 예외가 발생하는지 테스트
        Exception exception = assertThrows(
                NoticeException.class, () -> noticeService.deleteNotice(noticeId, otherMemberId));
        assertEquals(exception.getMessage(), ErrorCode.UNAUTHORIZED_LOGIN.getMessage());
    }

    private Member createMember() {
        return Member.builder()
                .email("user@example.com")
                .nickname("nickname")
                .password("password")
                .role(Role.USER)
                .build();
    }

    private Notice createNotice() {
        return Notice.builder()
                .member(member)
                .title("공지사항 제목")
                .content("공지사항 내용")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(7))
                .viewCount(0L)
                .build();
    }

}