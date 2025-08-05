package onlinecourseplatform.dto.responseDTOs;

import lombok.*;
import java.time.LocalDateTime;

// Response DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackResponseDTO {
    private int rating;
    private String review;
    private String reviewTitle;
}