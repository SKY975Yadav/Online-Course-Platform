package onlinecourseplatform.dto.responseDTOs;


import lombok.*;
// Response DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponseDTO {
    private Long moduleId;
    private String URL;
    private String filename;
}