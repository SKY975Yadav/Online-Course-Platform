package onlinecourseplatform.controller;

import onlinecourseplatform.dto.requestDTOs.FeedbackRequestDTO;
import onlinecourseplatform.dto.responseDTOs.FeedbackResponseDTO;
import onlinecourseplatform.service.FeedbackServices;
import onlinecourseplatform.utility.Utility;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/courses/feedbacks")
@RequiredArgsConstructor
@Slf4j
public class FeedbackController {

    private final FeedbackServices feedbackServices;
    private final Utility utility;

    /**
     * Get feedback by ID.
     */
    @Operation(summary = "Get Feedback by ID")
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponseDTO> getFeedbackById(@PathVariable Long id) {
        log.info("Fetching feedback with ID: {}", id);
        return ResponseEntity.ok(feedbackServices.getFeedback(id));
    }

    /**
     * Get all feedbacks for a course.
     */
    @Operation(summary = "Get all feedbacks of a course")
    @GetMapping("/course/{id}")
    public ResponseEntity<List<FeedbackResponseDTO>> getAllFeedbacks(@PathVariable Long id) {
        log.info("Fetching all feedbacks for course ID: {}", id);
        return ResponseEntity.ok(feedbackServices.getAllFeedbacks(id));
    }

    /**
     * Submit feedback for a course.
     */
    @Operation(summary = "Submit feedback for a course")
    @PostMapping("/course/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<FeedbackResponseDTO> setFeedback(@PathVariable Long id, @Valid @RequestBody FeedbackRequestDTO feedback, Principal principal) {
        Long studentId = utility.getUserIdFromPrincipal(principal);
        log.info("Student {} is submitting feedback for course {}", studentId, id);
        FeedbackResponseDTO response = feedbackServices.setFeedback(id, studentId, feedback);
        return ResponseEntity.ok(response);
    }
}
