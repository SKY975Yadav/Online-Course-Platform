package onlinecourseplatform.dto.entityDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleDTO {
    private Long id;
    private Long courseId;
    private String moduleName;
    private List<VideoDTO> videos;
    private List<DocumentDTO> documents;
}