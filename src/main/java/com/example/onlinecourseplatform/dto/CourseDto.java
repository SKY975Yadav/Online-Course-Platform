package com.example.onlinecourseplatform.dto;

import lombok.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDto {
    private Long id;

    private String title;

    private String description;

    private Long instructorId;

    private java.math.BigDecimal price;
}
