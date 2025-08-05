package onlinecourseplatform.dto.entityDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jdk.jshell.Snippet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackDTO {

    private Long id;

    private Long courseId;

    private Long studentId;

    private int rating;

    private String review;

    private String reviewTitle;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;


}
