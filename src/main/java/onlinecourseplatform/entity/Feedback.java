package onlinecourseplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private int rating; // Rating from 1 to 5

    @Column(length = 100)
    private String reviewTitle; // Optional title for the feedback

    @Column(length = 1000)
    private String review; // Optional feedback comment

    @Column(updatable = false)
    private LocalDateTime createdAt; // Timestamp for when the feedback was created
}