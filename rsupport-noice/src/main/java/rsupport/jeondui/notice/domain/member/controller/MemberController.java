package rsupport.jeondui.notice.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rsupport.jeondui.notice.common.jwt.JwtToken;
import rsupport.jeondui.notice.common.response.Response;
import rsupport.jeondui.notice.common.security.CustomUserDetails;
import rsupport.jeondui.notice.domain.member.controller.dto.request.MemberJoinRequest;
import rsupport.jeondui.notice.domain.member.controller.dto.request.MemberLoginRequest;
import rsupport.jeondui.notice.domain.member.controller.dto.response.MemberDetailsResponse;
import rsupport.jeondui.notice.domain.member.controller.dto.response.MemberLoginResponse;
import rsupport.jeondui.notice.domain.member.entity.Member;
import rsupport.jeondui.notice.domain.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public Response<Void> join(@ModelAttribute @Valid MemberJoinRequest request) {
        memberService.join(request);
        return Response.success(HttpStatus.CREATED, "회원 가입 성공!");
    }

    @PostMapping("/login")
    public Response<MemberLoginResponse> login(@ModelAttribute @Valid MemberLoginRequest request) {
        JwtToken jwtToken = memberService.login(request);
        return Response.success(HttpStatus.OK, "로그인 성공!", MemberLoginResponse.of(jwtToken));
    }

    @GetMapping
    public Response<MemberDetailsResponse> find(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = memberService.findById(userDetails.getId());
        return Response.success(HttpStatus.OK, "회원 정보 조회 성공!", MemberDetailsResponse.of(member));
    }
}
