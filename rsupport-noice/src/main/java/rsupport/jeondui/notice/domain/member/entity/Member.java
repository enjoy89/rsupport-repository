package rsupport.jeondui.notice.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rsupport.jeondui.notice.domain.member.controller.dto.request.MemberJoinRequest;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 회원 고유 ID

    @Column(name = "member_email", unique = true)
    private String email; // 회원 이메일

    @Column(name = "member_nickname", unique = true)
    private String nickname; // 회원 이름

    @Column(name = "member_password")
    private String password; // 회원 비밀번호

    @Enumerated(EnumType.STRING)
    private Role role; // 회원 권한

    @Builder
    private Member(String email, String nickname, String password, Role role) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
    }

    public static Member of(MemberJoinRequest request, String password) {
        return Member.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(password)
                .role(Role.USER)
                .build();
    }
}
