package rsupport.jeondui.notice.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rsupport.jeondui.notice.domain.notice.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
