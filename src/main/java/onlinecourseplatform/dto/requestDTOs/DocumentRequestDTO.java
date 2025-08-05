package onlinecourseplatform.dto.requestDTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

// Request DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRequestDTO {

    @NotBlank(message = "Document URL is required")
    private String URL;

    @NotBlank(message = "Document filename is required")
    private String filename;

}