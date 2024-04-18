package rsupport.jeondui.notice.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rsupport.jeondui.notice.common.response.Response;
import rsupport.jeondui.notice.domain.member.controller.dto.request.MemberJoinRequest;
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

}
