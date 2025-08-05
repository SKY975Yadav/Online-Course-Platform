package onlinecourseplatform.dto.responseDTOs;

import lombok.*;

import java.util.List;

// Response DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleResponseDTO {
    private Long id;
    private Long courseId;
    private String moduleName;
    private List<VideoResponseDTO> videos;
    private List<DocumentResponseDTO> documents;
}