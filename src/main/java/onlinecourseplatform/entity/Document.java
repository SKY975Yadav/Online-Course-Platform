package onlinecourseplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "document")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_url")
    private String URL;

    @Column(name = "document_filename")
    private String filename;

    @Column(name = "cloud_provider")
    @Enumerated(EnumType.STRING)
    private CloudProvider cloudProvider;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;
}
