package onlinecourseplatform.dto.responseDTOs;

import lombok.*;

// Response DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoResponseDTO {
    private Long moduleId;
    private String URL;
    private String filename;
    private String description;
}
