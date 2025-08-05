package onlinecourseplatform.utility;

import onlinecourseplatform.dto.entityDTOs.DocumentDTO;
import onlinecourseplatform.dto.entityDTOs.FeedbackDTO;
import onlinecourseplatform.dto.entityDTOs.ModuleDTO;
import onlinecourseplatform.dto.entityDTOs.VideoDTO;

import onlinecourseplatform.dto.requestDTOs.*;
import onlinecourseplatform.dto.responseDTOs.*;
import onlinecourseplatform.entity.*;
import lombok.RequiredArgsConstructor;
import onlinecourseplatform.entity.Module;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Conversion {

    // 🔁 Convert User entity → UserResponseDTO
    public UserResponseDTO toResponseDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    // 🔁  Convert UserRequestDTO to User Entity
    public User toEntityFromRequest(UserRequestDTO dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .role(dto.getRole())
                //Password Encoded and set in service layer
                //set createdAt in service layer
                .build();
    }

    //Convert Course Entity → ResponseDTO
// Convert Course Entity → CourseResponseDTO (includes modules, feedback, enrollments)
    public CourseResponseDTO toResponseDto(Course course) {
        return CourseResponseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .instructorId(course.getInstructorId())
                .price(course.getPrice())
                .modules(course.getModules().stream()
                        .map(this::toResponseDto) // Assuming you have a toResponseDto(Module module) method
                        .collect(Collectors.toList()))
                .feedbackList(course.getFeedbackList().stream()
                        .map(this::toResponseDto)
                        .collect(Collectors.toList()))
                .enrollmentList(course.getEnrollmentList().stream()
                        .map(this::toResponseDto)
                        .collect(Collectors.toList()))
                .createdAt(course.getCreatedAt())
                .build();
    }

    //Convert Course Entity → ResponseDTO
    public BasicCourseDetailsResponse toBasicCourseDto(Course course) {
        return BasicCourseDetailsResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .feedbackList(course.getFeedbackList().stream()
                        .map(this::toResponseDto)
                        .collect(Collectors.toList()))
                .build();
    }

    //Convert Course RequestDTO → Entity
    public Course toEntityFromRequest(CourseRequestDTO dto) {
        return Course.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                //set instructorId in service layer
                .price(dto.getPrice())
                .createdAt(LocalDateTime.now())
                .enrollmentList(new ArrayList<>()) // Initialize empty list
                .feedbackList(new ArrayList<>()) // Initialize empty list
                .modules(new ArrayList<>()) // Initialize empty list
                .build();
    }

    //Convert Enrollment Entity → ResponseDTO
    public EnrollmentResponseDTO toResponseDto(Enrollment enrollment) {
        return EnrollmentResponseDTO.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudentId())
                .courseId(enrollment.getCourse().getId())
                .enrolledAt(enrollment.getEnrolledAt())
                .completedAt(enrollment.getCompletedAt())
                .price(enrollment.getPrice())
                .status(enrollment.getStatus())
                .build();
    }

    // Convert Feedback Entity → FeedbackResponseDTO
    public FeedbackResponseDTO toResponseDto(Feedback feedback) {
        return FeedbackResponseDTO.builder()
                .rating(feedback.getRating())
                .review(feedback.getReview())
                .reviewTitle(feedback.getReviewTitle())
                .build();
    }

    // Convert Course Entity → CourseContentResponseDTO
    public CourseContentResponseDTO toCourseContentResponseDTO(Course course) {
        List<ModuleDTO> moduleDTOs = course.getModules().stream().map(this::toModuleDto).collect(Collectors.toList());

        return CourseContentResponseDTO.builder()
                .courseId(course.getId())
                .title(course.getTitle())
                .modules(moduleDTOs)
                .build();
    }

    // Convert Module Entity → ModuleDTO
    private ModuleDTO toModuleDto(Module module) {
        return ModuleDTO.builder()
                .id(module.getId())
                .courseId(module.getCourse().getId())
                .moduleName(module.getModuleName())
                .videos(module.getVideos().stream()
                        .map(this::toVideoDto)
                        .collect(Collectors.toList()))
                .documents(module.getDocuments().stream()
                        .map(this::toDocumentDto)
                        .collect(Collectors.toList()))
                .build();
    }

    // Entity → DTO
    private DocumentDTO toDocumentDto(Document document) {
        return DocumentDTO.builder()
                .id(document.getId())
                .moduleId(document.getModule().getId())
                .URL(document.getURL())
                .filename(document.getFilename())
                .cloudProvider(document.getCloudProvider())
                .build();
    }

    // Entity → DTO
    private VideoDTO toVideoDto(Video video) {
        return VideoDTO.builder()
                .id(video.getId())
                .moduleId(video.getModule().getId())
                .URL(video.getURL())
                .filename(video.getFilename())
                .description(video.getDescription())
                .cloudProvider(video.getCloudProvider())
                .build();
    }

    // Entity → ResponseDTO
    public ModuleResponseDTO toResponseDto(Module module) {
        return ModuleResponseDTO.builder()
                .id(module.getId())
                .courseId(module.getCourse().getId())
                .moduleName(module.getModuleName())
                .videos(module.getVideos().stream()
                        .map(this::toResponseDto)
                        .collect(Collectors.toList()))
                .documents(module.getDocuments().stream()
                        .map(this::toResponseDto)
                        .collect(Collectors.toList()))
                .build();
    }

    // RequestDTO → Entity
    public Module toEntityFromRequest(ModuleRequestDTO dto) {
        return Module.builder()
                .moduleName(dto.getModuleName())
                // courseContent will be set in service layer
                .build();
    }

    // Entity → ResponseDTO
    public VideoResponseDTO toResponseDto(Video video) {
        return VideoResponseDTO.builder()
                .moduleId(video.getModule().getId())
                .URL(video.getURL())
                .filename(video.getFilename())
                .description(video.getDescription())
                .build();
    }

    // RequestDTO → Entity
    public Video toEntityFromRequest(VideoRequestDTO dto) {
        return Video.builder()
                .URL(dto.getURL())
                .filename(dto.getFilename())
                .description(dto.getDescription())
                // others will be set in service layer
                .build();
    }

    // Entity → ResponseDTO
    public DocumentResponseDTO toResponseDto(Document document) {
        return DocumentResponseDTO.builder()
                .moduleId(document.getModule().getId())
                .URL(document.getURL())
                .filename(document.getFilename())
                .build();
    }

    // RequestDTO → Entity
    public Document toEntityFromRequest(DocumentRequestDTO dto) {
        return Document.builder()
                .URL(dto.getURL())
                .filename(dto.getFilename())
                // others will be set in service layer
                .build();
    }

    // Entity → DTO
    public FeedbackDTO toDto(Feedback feedback) {
        return FeedbackDTO.builder()
                .id(feedback.getId())
                .courseId(feedback.getCourse().getId())
                .studentId(feedback.getStudentId())
                .rating(feedback.getRating())
                .review(feedback.getReview())
                .reviewTitle(feedback.getReviewTitle())
                .createdAt(feedback.getCreatedAt())
                .build();
    }

    // DTO → Entity
    public Feedback toEntity(FeedbackDTO dto, Course course) {
        return Feedback.builder()
                .id(dto.getId())
                .course(course)
                .studentId(dto.getStudentId())
                .rating(dto.getRating())
                .review(dto.getReview())
                .reviewTitle(dto.getReviewTitle())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    // RequestDTO → Entity
    public Feedback toEntityFromRequest(FeedbackRequestDTO dto, Course course) {
        return Feedback.builder()
                .course(course)
                .rating(dto.getRating())
                .review(dto.getReview())
                .reviewTitle(dto.getReviewTitle())
                // set createdAt in service layer
                .build();
    }
}