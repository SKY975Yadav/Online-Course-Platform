package onlinecourseplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "video")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "video_url")
    private String URL;

    @Column(name = "video_filename")
    private String filename;

    @Column(name = "cloud_provider")
    @Enumerated(EnumType.STRING)
    private CloudProvider cloudProvider;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;
}