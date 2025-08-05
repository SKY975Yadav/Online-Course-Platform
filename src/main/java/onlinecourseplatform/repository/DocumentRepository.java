package onlinecourseplatform.repository;

import onlinecourseplatform.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    void deleteByModuleId(Long id);
}
