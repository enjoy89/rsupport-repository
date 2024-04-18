package rsupport.jeondui.notice.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rsupport.jeondui.notice.common.exception.ErrorCode;
import rsupport.jeondui.notice.common.exception.custom.MemberException;
import rsupport.jeondui.notice.common.jwt.JwtTokenProvider;
import rsupport.jeondui.notice.domain.member.controller.dto.request.MemberJoinRequest;
import rsupport.jeondui.notice.domain.member.entity.Member;
import rsupport.jeondui.notice.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    /**
     * 회원 가입
     */
    public void join(MemberJoinRequest request) {

        // 사용자 입력값에 대한 검증을 마친 후, 회원가입 로직 실행
        validateExistingMember(request.getEmail());
        validateExistingNickname(request.getNickname());
        validatePassword(request.getPassword(), request.getPasswordConfirm());

        memberRepository.save(Member.of(request, passwordEncoder.encode(request.getPassword())));
    }

    /**
     * Id를 통해 회원 조회
     */
    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));
    }

    /**
     * 이미 존재하는 회원인지 검증
     */
    private void validateExistingMember(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new MemberException(ErrorCode.MEMBER_ALREADY_EXISTS);
        }
    }

    /**
     * 이미 존재하는 닉네임인지 검증
     */
    private void validateExistingNickname(String nickname) {
        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new MemberException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    /**
     * 비밀번호와 비밀번호 확인값이 일치하는지 검증
     */
    private void validatePassword(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new MemberException(ErrorCode.PASSWORD_MISMATCH);
        }
    }
}
