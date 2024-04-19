package rsupport.jeondui.notice.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import rsupport.jeondui.notice.common.exception.ErrorCode;
import rsupport.jeondui.notice.common.exception.custom.MemberException;
import rsupport.jeondui.notice.common.jwt.JwtToken;
import rsupport.jeondui.notice.domain.member.controller.dto.request.MemberJoinRequest;
import rsupport.jeondui.notice.domain.member.controller.dto.request.MemberLoginRequest;
import rsupport.jeondui.notice.domain.member.entity.Member;
import rsupport.jeondui.notice.domain.member.entity.Role;
import rsupport.jeondui.notice.domain.member.repository.MemberRepository;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository;

    private String email;
    private String nickname;
    private String password;
    private String passwordConfirm;

    @BeforeEach
    void setUp() {
        // 공통적으로 사용되는 테스트 데이터 설정
        email = "test@example.com";
        password = "password123@";
        passwordConfirm = "password123@";
        nickname = "testUser";

        // 회원 가입 및 로그인 테스트를 위한 사전 조건 설정
        MemberJoinRequest joinRequest = new MemberJoinRequest(email, nickname, password, passwordConfirm);
        Member member = Member.of(joinRequest, passwordEncoder.encode(password));
        memberRepository.save(member);
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 가입 성공 테스트")
    void 회원_가입_성공() {
        // given
        String newEmail = "newTest@example.com"; // 기존 데이터와 충돌 방지를 위해 새 이메일 사용
        String newNickname = "newUser";
        MemberJoinRequest newRequest = new MemberJoinRequest(newEmail, newNickname, password, passwordConfirm);

        // when
        Member member = memberService.join(newRequest);

        // then
        assertThat(member.getEmail()).isEqualTo(newEmail);
        assertThat(member.getNickname()).isEqualTo(newNickname);
        assertThat(member.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("회원 가입 실패 테스트")
    void 회원_가입_실패() {
        // given
        String newEmail = "newTest@example.com"; // 기존 데이터와 충돌 방지를 위해 새 이메일 사용
        String newNickname = "newUser";
        String mismatchPasswordConfirm = "password1234@";
        MemberJoinRequest mismatchRequest = new MemberJoinRequest(newEmail, newNickname, password, mismatchPasswordConfirm);

        // when & then
        // 비밀번호 불일치 예외가 발생하는지 테스트
        Exception exception = assertThrows(MemberException.class, () -> memberService.join(mismatchRequest));
        assertEquals(exception.getMessage(), ErrorCode.PASSWORD_MISMATCH.getMessage());
    }

    @Test
    @DisplayName("로그인 테스트")
    void 로그인() {
        // given
        MemberLoginRequest loginRequest = new MemberLoginRequest(email, password);

        // when
        JwtToken jwtToken = memberService.login(loginRequest);

        // then
        assertNotNull(jwtToken); // 토큰이 발급되었는지 확인
    }

    @Test
    @DisplayName("이메일을 통한 사용자 조회 테스트")
    void 이메일로_사용자_조회() {
        // when
        Member foundMember = memberService.findByEmail(email);

        // then
        assertNotNull(foundMember);
        assertThat(foundMember.getEmail()).isEqualTo(email);
        assertThat(foundMember.getNickname()).isEqualTo(nickname);
    }
}
