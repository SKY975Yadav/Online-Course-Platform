package onlinecourseplatform.dto.responseDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasicCourseDetailsResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private List<FeedbackResponseDTO> feedbackList;
}