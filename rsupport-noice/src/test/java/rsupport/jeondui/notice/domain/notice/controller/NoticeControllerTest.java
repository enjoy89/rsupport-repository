package rsupport.jeondui.notice.domain.notice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import rsupport.jeondui.notice.common.jwt.JwtToken;
import rsupport.jeondui.notice.common.jwt.JwtTokenProvider;
import rsupport.jeondui.notice.common.jwt.JwtTokenValidationResult;
import rsupport.jeondui.notice.common.security.CustomUserDetails;
import rsupport.jeondui.notice.domain.member.controller.dto.request.MemberLoginRequest;
import rsupport.jeondui.notice.domain.member.entity.Member;
import rsupport.jeondui.notice.domain.member.entity.Role;
import rsupport.jeondui.notice.domain.member.service.MemberService;
import rsupport.jeondui.notice.domain.notice.controller.dto.request.NoticeModifyRequest;
import rsupport.jeondui.notice.domain.notice.controller.dto.request.NoticeRegisterRequest;
import rsupport.jeondui.notice.domain.notice.controller.dto.response.NoticeDetailResponse;
import rsupport.jeondui.notice.domain.notice.controller.dto.response.PagedNoticeResponse;
import rsupport.jeondui.notice.domain.notice.service.NoticeService;

@SpringBootTest
@AutoConfigureMockMvc
class NoticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private MemberService memberService;

    @MockBean
    private NoticeService noticeService;

    private Member member;
    private JwtToken jwtToken;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        member = createMember();
        jwtToken = JwtToken.builder()
                .grantType("Bearer")
                .accessToken("testAccessToken")
                .build();

        // Mocking JWT Token 생성
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn(jwtToken);
        when(memberService.login(any(MemberLoginRequest.class))).thenReturn(jwtToken);
        when(jwtTokenProvider.getAuthentication(anyString())).thenReturn(
                new UsernamePasswordAuthenticationToken(member, null));

        // Mock JWT 토큰 생성 메서드 호출
        when(jwtTokenProvider.getAuthentication(anyString())).thenReturn(
                new UsernamePasswordAuthenticationToken(
                        CustomUserDetails.builder()
                                .id(1L)
                                .email("test@example.com")
                                .password("") // 비밀번호는 필요 없으므로 빈 문자열 설정
                                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                                .build(),
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );
        when(jwtTokenProvider.validateToken(jwtToken.getAccessToken())).thenReturn(JwtTokenValidationResult.valid());
        mockFile = new MockMultipartFile("file", "uploadedFileName.txt", "text/plain", "Some contents".getBytes());
    }

    @Test
    @DisplayName("공지사항 등록 API 테스트")
    void 공지사항_등록() throws Exception {
        // given
        NoticeRegisterRequest registerRequest = new NoticeRegisterRequest("공지사항 제목", "공지사항 내용", LocalDateTime.now(),
                LocalDateTime.now().plusDays(7), Collections.singletonList(mockFile));

        // when
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/notices")
                        .file(mockFile)
                        .header("Authorization", "Bearer testAccessToken")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("title", registerRequest.getTitle())
                        .param("content", registerRequest.getContent())
                        .param("startDateTime", registerRequest.getStartDateTime().toString())
                        .param("endDateTime", registerRequest.getEndDateTime().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("공지사항 등록 성공!"));

        // then
        verify(noticeService).registerNotice(eq(1L), any(NoticeRegisterRequest.class));
    }

    @Test
    @DisplayName("공지사항 수정 API 테스트")
    void 공지사항_수정() throws Exception {
        // given
        NoticeModifyRequest modifyRequest = new NoticeModifyRequest("수정 공지사항 제목", "수정 공지사항 내용", LocalDateTime.now(),
                LocalDateTime.now().plusDays(7), Collections.singletonList(mockFile), List.of(1L));

        // when
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/notices/{noticeId}", 1L)
                        .file(mockFile)
                        .header("Authorization", "Bearer testAccessToken")
                        .param("title", modifyRequest.getTitle())
                        .param("content", modifyRequest.getContent())
                        .param("startDateTime", modifyRequest.getStartDateTime().toString())
                        .param("endDateTime", modifyRequest.getEndDateTime().toString())
                        .param("attachmentIds", modifyRequest.getDeleteAttachmentIds().toString()) // 파일 대신 파일 관련 정보(예: ID)를 전송
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("공지사항 수정 성공!"));

        // then
        verify(noticeService).modifyNotice(eq(1L), eq(1L), any(NoticeModifyRequest.class));
    }

    @Test
    @DisplayName("공지사항 전체 조회 API 테스트")
    void 공지사항_전체_조회() throws Exception {
        // given
        PagedNoticeResponse pagedNoticeResponse = PagedNoticeResponse.builder()
                .totalItems(10)
                .totalPages(10)
                .currentPage(0)
                .pageSize(10)
                .items(Collections.emptyList())
                .build();
        when(noticeService.findAll(any(Pageable.class))).thenReturn(pagedNoticeResponse);

        // when & then
        mockMvc.perform(get("/api/notices")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("공지사항 전체 조회 성공!"))
                .andExpect(jsonPath("$.data.totalItems").value(10))
                .andExpect(jsonPath("$.data.totalPages").value(10))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items.length()").value(0)); // 예상되는 공지사항 개수와 일치하는지 확인

        // then
        verify(noticeService).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("공지사항 상세 조회 API 테스트")
    void 공지사항_상세_조회() throws Exception {
        // given
        NoticeDetailResponse noticeDetailResponse = mock();
        when(noticeService.findById(1L)).thenReturn(noticeDetailResponse);

        // when
        mockMvc.perform(get("/api/notices/{noticeId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("공지사항 상세 조회 성공!"));

        // then
        verify(noticeService).findById(1L);
    }

    @Test
    @DisplayName("공지사항 삭제 API 테스트")
    void 공지사항_삭제() throws Exception {
        // given
        doNothing().when(noticeService).deleteNotice(eq(1L), anyLong());

        // when & then
        mockMvc.perform(delete("/api/notices/{noticeId}", 1L)
                        .header("Authorization", "Bearer testAccessToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("공지사항 삭제 성공!"));

        // then
        verify(noticeService).deleteNotice(eq(1L), anyLong());

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

}