package onlinecourseplatform.dto.requestDTOs;

import onlinecourseplatform.dto.entityDTOs.ModuleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

// Request DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseUpdateRequest {

    private String title;

    private String description;

    private BigDecimal price;

    private List<ModuleDTO> modules;
}