package onlinecourseplatform.dto.responseDTOs;

import onlinecourseplatform.dto.entityDTOs.ModuleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseContentResponseDTO {
    private Long courseId;
    private String title;
    private List<ModuleDTO> modules;
}

