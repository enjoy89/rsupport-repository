package rsupport.jeondui.notice.domain.notice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rsupport.jeondui.notice.common.base.BaseTimeEntity;
import rsupport.jeondui.notice.domain.attachment.entity.Attachment;
import rsupport.jeondui.notice.domain.member.entity.Member;
import rsupport.jeondui.notice.domain.notice.controller.dto.request.NoticeRegisterRequest;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id; // 공지사항 고유 ID

    @Column(name = "notice_title")
    private String title; // 공지사항 제목

    @Column(name = "notice_content")
    private String content; // 공지사항 내용

    @Column(name = "notice_start_date")
    private LocalDateTime startDateTime; // 공지사항 시작 일시

    @Column(name = "notice_end_date")
    private LocalDateTime endDateTime; // 공지사항 종료 일시

    @OneToMany(mappedBy = "notice", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>(); // 공지사항 첨부 파일 리스트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "notice_view_count")
    private Long viewCount;

    @Builder
    private Notice(Member member, String title, String content, LocalDateTime startDateTime, LocalDateTime endDateTime, Long viewCount) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.viewCount = viewCount;
    }

    public static Notice of(Member member, NoticeRegisterRequest request) {
        return Notice.builder()
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .viewCount(0L) // 초기화
                .build();
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

}
