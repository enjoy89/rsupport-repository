package rsupport.jeondui.notice.domain.notice.controller.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import rsupport.jeondui.notice.domain.notice.entity.Notice;

@Getter
public class NoticeResponse {

    private Long noticeId;              // 공지사항 고유 번호
    private String title;               // 공지사항 제목
    private Long viewCount;             // TODO: 조회수
    private LocalDateTime createdAt;    // 등록 일시
    private String writer;              // 작성자

    @Builder
    private NoticeResponse(Long noticeId, String title, Long viewCount, LocalDateTime createdAt, String writer) {
        this.noticeId = noticeId;
        this.title = title;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.writer = writer;
    }

    public static NoticeResponse of(Notice notice) {
        return NoticeResponse.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .viewCount(0L)
                .createdAt(notice.getCreatedAt())
                .writer(notice.getMember().getNickname())
                .build();
    }
}
