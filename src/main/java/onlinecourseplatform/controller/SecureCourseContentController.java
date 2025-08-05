package onlinecourseplatform.controller;

import onlinecourseplatform.entity.Course;
import onlinecourseplatform.entity.Document;
import onlinecourseplatform.entity.Video;
import onlinecourseplatform.repository.DocumentRepository;
import onlinecourseplatform.repository.VideoRepository;
import onlinecourseplatform.service.EnrollmentService;
import onlinecourseplatform.service.SecureContentStreamingService;
import onlinecourseplatform.utility.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/secure/content")
@RequiredArgsConstructor
@Slf4j
public class SecureCourseContentController {

    private final EnrollmentService enrollmentService;
    private final SecureContentStreamingService streamingService;
    private final Utility utility;
    private final VideoRepository videoRepository;
    private final DocumentRepository documentRepository;

    /**
     * Streams video content securely, ensuring the user has the right permissions.
     */
    @GetMapping("/video/{videoId}")
    public ResponseEntity<Resource> streamVideo(@PathVariable Long videoId) {

        Long userId = utility.getCurrentUserId();
        String role = utility.getCurrentUserRole();

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found with ID: " + videoId));

        Course course = video.getModule().getCourse();
        Long courseId = course.getId();

        boolean isAuthorized = role.equals("ADMIN")
                || (role.equals("INSTRUCTOR") && course.getInstructorId().equals(userId))
                || enrollmentService.isEnrolled(userId, courseId);

        if (!isAuthorized) {
            log.warn("User {} is not authorized to access video {} of course {}", userId, videoId, courseId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        log.info("User {} is accessing video {} from course {}", userId, videoId, courseId);
        return streamingService.streamVideoContent(video.getURL(), video.getFilename());
    }

    /**
     * Streams document content securely, ensuring the user has the right permissions.
     */
    @GetMapping("/document/{documentId}")
    public ResponseEntity<Resource> streamDocument(@PathVariable Long documentId) {

        Long userId = utility.getCurrentUserId();
        String role = utility.getCurrentUserRole();

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + documentId));

        Course course = document.getModule().getCourse();
        Long courseId = course.getId();

        boolean isAuthorized = role.equals("ADMIN")
                || (role.equals("INSTRUCTOR") && course.getInstructorId().equals(userId))
                || enrollmentService.isEnrolled(userId, courseId);

        if (!isAuthorized) {
            log.warn("User {} is not authorized to access document {} of course {}", userId, documentId, courseId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        log.info("User {} is accessing document {} from course {}", userId, documentId, courseId);
        return streamingService.streamDocumentContent(document.getURL(), document.getFilename());
    }
}
