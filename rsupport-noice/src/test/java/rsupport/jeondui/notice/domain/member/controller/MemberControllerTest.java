package rsupport.jeondui.notice.domain.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import rsupport.jeondui.notice.common.jwt.JwtToken;
import rsupport.jeondui.notice.common.jwt.JwtTokenProvider;
import rsupport.jeondui.notice.common.jwt.JwtTokenValidationResult;
import rsupport.jeondui.notice.common.security.CustomUserDetails;
import rsupport.jeondui.notice.domain.member.controller.dto.request.MemberJoinRequest;
import rsupport.jeondui.notice.domain.member.controller.dto.request.MemberLoginRequest;
import rsupport.jeondui.notice.domain.member.entity.Member;
import rsupport.jeondui.notice.domain.member.entity.Role;
import rsupport.jeondui.notice.domain.member.service.MemberService;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private MemberService memberService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("회원 가입 API 테스트")
    void 회원_가입() throws Exception {
        // given
        MemberJoinRequest request = new MemberJoinRequest("test@example.com", "testUser", "password123@",
                "password123@");

        // when
        mockMvc.perform(post("/api/members/join")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("email", request.getEmail())
                        .param("nickname", request.getNickname())
                        .param("password", request.getPassword())
                        .param("passwordConfirm", request.getPasswordConfirm()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("회원 가입 성공!"));

        // then
        verify(memberService).join(any(MemberJoinRequest.class));
    }

    @Test
    @DisplayName("로그인 API 테스트")
    void 로그인() throws Exception {
        // given
        MemberLoginRequest loginRequest = new MemberLoginRequest("test@example.com", "password123@");

        JwtToken jwtToken = JwtToken.builder()
                .grantType("Bearer")
                .accessToken("testAccessToken")
                .build();

        // Mocking JWT Token 생성
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn(jwtToken);
        when(memberService.login(any(MemberLoginRequest.class))).thenReturn(jwtToken);

        // when & then
        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("email", loginRequest.getEmail())
                        .param("password", loginRequest.getPassword()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("로그인 성공!"))
                .andExpect(jsonPath("$.data.accessToken").value("testAccessToken"));
    }

    @Test
    @DisplayName("회원 정보 조회 API 테스트")
    void 회원_조회() throws Exception {
        // given
        // Mock JWT 토큰 생성
        JwtToken jwtToken = JwtToken.builder()
                .grantType("Bearer")
                .accessToken("testAccessToken")
                .build();

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

        // Mock MemberService의 findById 메서드 호출
        when(memberService.findById(anyLong())).thenReturn(
                Member.builder()
                        .email("test@example.com")
                        .nickname("testUser")
                        .role(Role.USER)
                        .build()
        );

        when(jwtTokenProvider.validateToken(jwtToken.getAccessToken())).thenReturn(JwtTokenValidationResult.valid());

        // when & then
        mockMvc.perform(get("/api/members")
                        .header("Authorization", "Bearer testAccessToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("회원 정보 조회 성공!"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.nickname").value("testUser"))
                .andExpect(jsonPath("$.data.role").value("USER"));

        // then
        verify(memberService).findById(anyLong());
    }
}