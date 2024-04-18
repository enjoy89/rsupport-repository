package rsupport.jeondui.notice.domain.notice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rsupport.jeondui.notice.common.response.Response;
import rsupport.jeondui.notice.common.security.CustomUserDetails;
import rsupport.jeondui.notice.domain.notice.controller.dto.request.NoticeRegisterRequest;
import rsupport.jeondui.notice.domain.notice.service.NoticeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 공지사항 등록 API (비회원 불가능)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<Void> registerNotice(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @ModelAttribute @Valid NoticeRegisterRequest request) {
        noticeService.registerNotice(userDetails.getId(), request);
        return Response.success(HttpStatus.CREATED, "공지사항 등록 성공!");
    }

    // TODO: 공지사항 전체, 상세 조회 API
}
