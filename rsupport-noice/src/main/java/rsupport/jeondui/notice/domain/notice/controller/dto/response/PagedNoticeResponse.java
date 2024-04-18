package rsupport.jeondui.notice.domain.notice.controller.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import rsupport.jeondui.notice.domain.notice.entity.Notice;

@Getter
public class PagedNoticeResponse {
    private long totalItems;                // 전체 공지사항 개수
    private int totalPages;                 // 전체 페이지 수
    private int currentPage;                // 현재 페이지 수
    private int pageSize;                   // 페이지당 공지사항 개수
    private List<NoticeResponse> items;     // 공지사항 정보 목록

    @Builder
    private PagedNoticeResponse(long totalItems, int totalPages, int currentPage, int pageSize,
                                List<NoticeResponse> items) {
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.items = items;
    }

    public static PagedNoticeResponse of(Page<Notice> notices) {
        List<NoticeResponse> noticeResponses = notices.getContent().stream()
                .map(NoticeResponse::of)
                .toList();

        return PagedNoticeResponse.builder()
                .totalItems(notices.getTotalElements())
                .totalPages(notices.getTotalPages())
                .currentPage(notices.getNumber())
                .pageSize(notices.getSize())
                .items(noticeResponses)
                .build();
    }
}
