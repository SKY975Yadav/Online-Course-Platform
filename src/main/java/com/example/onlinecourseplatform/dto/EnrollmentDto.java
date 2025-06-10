package com.example.onlinecourseplatform.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDto {
    private Long id;
    private Long studentId;
    private Long courseId;
    private LocalDateTime enrolledAt;
}

