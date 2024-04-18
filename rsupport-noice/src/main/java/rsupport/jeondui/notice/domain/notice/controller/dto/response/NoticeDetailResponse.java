package rsupport.jeondui.notice.domain.notice.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import rsupport.jeondui.notice.domain.notice.entity.Notice;

@Getter
public class NoticeDetailResponse {

    private Long noticeId;                  // 공지사항 고유 번호
    private String title;                   // 공지사항 제목
    private String content;                 // 공지사항 내용
    private LocalDateTime startDateTime;    // 공지사항 시작 일시
    private LocalDateTime endDateTime;      // 공지사항 종료 일시
    private LocalDateTime createdAt;        // 공지사항 등록 일시
    private LocalDateTime modifiedAt;       // 공지사항 최종 수정 일시
    private String writer;                  // 작성자
    private Long viewCount;                 // TODO: 조회수
    private List<String> fileUrls;          // 첨부파일 목록 URL 리스트

    @Builder
    private NoticeDetailResponse(Long noticeId, String title, String content, LocalDateTime startDateTime,
                                 LocalDateTime endDateTime, LocalDateTime createdAt, LocalDateTime modifiedAt,
                                 String writer,
                                 Long viewCount, List<String> fileUrls) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.writer = writer;
        this.viewCount = viewCount;
        this.fileUrls = fileUrls;
    }

    public static NoticeDetailResponse of(Notice notice, List<String> fileUrls) {
        return NoticeDetailResponse.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .startDateTime(notice.getStartDateTime())
                .endDateTime(notice.getEndDateTime())
                .createdAt(notice.getCreatedAt())
                .modifiedAt(notice.getModifiedAt())
                .writer(notice.getMember().getNickname())
                .viewCount(0L)
                .fileUrls(fileUrls)
                .build();
    }
}
