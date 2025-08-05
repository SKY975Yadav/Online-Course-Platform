package onlinecourseplatform.dto.requestDTOs;

import lombok.*;
import jakarta.validation.constraints.*;

// Request DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackRequestDTO {

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private int rating;

    @Size(max = 100, message = "Review title must be less than 100 characters")
    private String reviewTitle;

    @Size(max = 500, message = "Review must be less than 500 characters")
    private String review;

}