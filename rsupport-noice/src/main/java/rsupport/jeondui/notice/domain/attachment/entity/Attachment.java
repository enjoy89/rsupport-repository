package rsupport.jeondui.notice.domain.attachment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rsupport.jeondui.notice.domain.notice.entity.Notice;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long id; // 첨부파일 고유 ID

    @Column(name = "attachment_file_name")
    private String fileName; // 첨부파일 이름

    @Column(name = "attachment_file_type")
    private String fileType; // 첨부파일 타입 (파일 확장자)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @Builder
    private Attachment(Notice notice, String fileName, String fileType) {
        this.notice = notice;
        this.fileName = fileName;
        this.fileType = fileType;
    }

    public static Attachment of(Notice notice, String fileName, String fileType) {
        return Attachment.builder()
                .notice(notice)
                .fileName(fileName)
                .fileType(fileType)
                .build();
    }
}
