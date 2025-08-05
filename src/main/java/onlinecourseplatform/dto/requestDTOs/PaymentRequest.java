package onlinecourseplatform.dto.requestDTOs;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    private Long courseId;
    private Double amount;
}

