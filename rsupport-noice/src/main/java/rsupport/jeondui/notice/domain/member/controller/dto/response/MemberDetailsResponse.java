package rsupport.jeondui.notice.domain.member.controller.dto.response;

import lombok.Builder;
import lombok.Getter;
import rsupport.jeondui.notice.domain.member.entity.Member;
import rsupport.jeondui.notice.domain.member.entity.Role;

@Getter
public class MemberDetailsResponse {

    private Long memberId;
    private String email;
    private String nickname;
    private Role role;

    @Builder
    private MemberDetailsResponse(Long memberId, String email, String nickname, Role role) {
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }

    public static MemberDetailsResponse of(Member member) {
        return MemberDetailsResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole())
                .build();
    }
}
