package com.example.onlinecourseplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentBodyOutput {
    private EnrollmentDto enrollment;
    private String message;
}
