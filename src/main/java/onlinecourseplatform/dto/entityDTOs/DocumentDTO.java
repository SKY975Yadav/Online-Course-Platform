package onlinecourseplatform.dto.entityDTOs;

import onlinecourseplatform.entity.CloudProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDTO {
    private Long id;
    private Long moduleId;
    private String URL;
    private String filename;
    private CloudProvider cloudProvider;
}
