package onlinecourseplatform.repository;

import onlinecourseplatform.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video,Long> {
    void deleteByModuleId(Long id);
    List<Video> findByModuleId(Long moduleId); // optional
}
