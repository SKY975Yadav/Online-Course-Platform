package onlinecourseplatform.dto.requestDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

// Request DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoRequestDTO {

    @NotBlank(message = "Video URL is required")
    private String URL;

    @NotBlank(message = "Video filename is required")
    private String filename;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;
}
