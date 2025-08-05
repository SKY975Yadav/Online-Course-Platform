package onlinecourseplatform.service;

import onlinecourseplatform.dto.requestDTOs.FeedbackRequestDTO;
import onlinecourseplatform.dto.responseDTOs.FeedbackResponseDTO;
import onlinecourseplatform.entity.Course;
import onlinecourseplatform.entity.Feedback;
import onlinecourseplatform.repository.CourseRepository;
import onlinecourseplatform.repository.FeedbackRepository;
import onlinecourseplatform.utility.Conversion;
import onlinecourseplatform.utility.Utility;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServices {

    private final CourseRepository courseRepository;
    private final Conversion conversion;
    private final Utility utility;
    private final FeedbackRepository feedbackRepository;

    /**
     * Get feedback by ID.
     */
    public FeedbackResponseDTO getFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
        log.info("Fetched feedback with ID: {}", id);
        return conversion.toResponseDto(feedback);
    }

    /**
     * Get all feedbacks for a course.
     */
    public List<FeedbackResponseDTO> getAllFeedbacks(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + id));
        log.info("Found {} feedbacks for course ID {}", course.getFeedbackList().size(), id);
        return course.getFeedbackList().stream()
                .map(conversion::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Submit feedback for a course.
     */
    public FeedbackResponseDTO setFeedback(Long courseId, Long studentId, @Valid FeedbackRequestDTO feedback) {
        utility.validateEnrollment(studentId, courseId);

        boolean alreadySubmitted = feedbackRepository.existsByCourseIdAndStudentId(courseId, studentId);
        if (alreadySubmitted) {
            log.warn("Student {} has already submitted feedback for course {}", studentId, courseId);
            throw new RuntimeException("You have already submitted feedback for this course.");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));

        Feedback newFeedback = Feedback.builder()
                .course(course)
                .studentId(studentId)
                .rating(feedback.getRating())
                .reviewTitle(feedback.getReviewTitle())
                .review(feedback.getReview())
                .createdAt(LocalDateTime.now())
                .build();

        Feedback savedFeedback = feedbackRepository.save(newFeedback);

        log.info("Student {} submitted feedback for course {}", studentId, courseId);
        return conversion.toResponseDto(savedFeedback);
    }
}
