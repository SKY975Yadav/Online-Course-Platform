package onlinecourseplatform.dto.responseDTOs;

import onlinecourseplatform.dto.entityDTOs.ModuleDTO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Long instructorId;
    private BigDecimal price;
    private List<ModuleResponseDTO> modules;
    private List<FeedbackResponseDTO> feedbackList;
    private LocalDateTime createdAt;
    private List<EnrollmentResponseDTO> enrollmentList;
}
