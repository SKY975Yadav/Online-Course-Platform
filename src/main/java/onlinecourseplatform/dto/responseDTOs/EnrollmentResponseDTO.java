package onlinecourseplatform.dto.responseDTOs;

import onlinecourseplatform.entity.Status;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Response DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentResponseDTO {
    private Long id;
    private Long studentId;
    private Long courseId;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    private BigDecimal price;
    private Status status;
}