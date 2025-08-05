package onlinecourseplatform.dto.requestDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import onlinecourseplatform.dto.entityDTOs.DocumentDTO;
import onlinecourseplatform.dto.entityDTOs.VideoDTO;

import java.util.List;

// Request DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleRequestDTO {

    @NotBlank(message = "Module name is required")
    @Size(max = 100, message = "Module name must be less than 100 characters")
    private String moduleName;

    @NotNull(message = "Videos are required")
    private List<VideoRequestDTO> videos;

    @NotNull(message = "Documents are required")
    private List<DocumentRequestDTO> documents;
}