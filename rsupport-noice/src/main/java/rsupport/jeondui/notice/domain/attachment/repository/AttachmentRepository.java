package rsupport.jeondui.notice.domain.attachment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rsupport.jeondui.notice.domain.attachment.entity.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
