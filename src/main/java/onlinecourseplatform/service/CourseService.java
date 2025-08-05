package onlinecourseplatform.service;

import onlinecourseplatform.dto.entityDTOs.ModuleDTO;
import onlinecourseplatform.dto.entityDTOs.DocumentDTO;
import onlinecourseplatform.dto.entityDTOs.VideoDTO;
import onlinecourseplatform.dto.requestDTOs.*;
import onlinecourseplatform.dto.responseDTOs.BasicCourseDetailsResponse;
import onlinecourseplatform.dto.responseDTOs.CourseContentResponseDTO;
import onlinecourseplatform.dto.responseDTOs.CourseResponseDTO;
import onlinecourseplatform.dto.responseDTOs.UserResponseDTO;
import onlinecourseplatform.entity.*;
import onlinecourseplatform.entity.Module;
import onlinecourseplatform.repository.*;
import onlinecourseplatform.utility.Conversion;
import onlinecourseplatform.utility.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Service class for managing courses and instructor-related actions.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ModuleRepository moduleRepository;
    private final VideoRepository videoRepository;
    private final DocumentRepository documentRepository;
    private final Conversion conversion;
    private final Utility utility;
    private final CloudUrlProcessorService cloudUrlProcessorService;

    /**
     * Retrieves all courses available on the platform.
     */
    public List<BasicCourseDetailsResponse> getAllCourses() {
        return courseRepository.findAll().stream().map(conversion::toBasicCourseDto).collect(Collectors.toList());
    }

    /**
     * Retrieves course details by its ID for students
     */
    public BasicCourseDetailsResponse getCourseForStudent(Long id) {
        Course course =  courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + id));
        return conversion.toBasicCourseDto(course);
    }

    /**
     * Retrieves course details by its ID for instructor or Admin
     */
    public CourseResponseDTO getFullCourseForInstructor(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + id));
        return conversion.toResponseDto(course); // full course details with content, feedback, etc.
    }
    /**
     * Retrieves all courses created by a specific instructor.
     */
    public List<CourseResponseDTO> getCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructorId(instructorId).stream().map(conversion::toResponseDto).collect(Collectors.toList());
    }

    @Transactional
    public CourseResponseDTO createCourse(CourseRequestDTO courseDto, Long instructorId) {
        Course newCourse = conversion.toEntityFromRequest(courseDto);// includes title, description, price,created At, feedbackList, enrollmentList
        newCourse.setInstructorId(instructorId);

        // Save course first to generate ID
        newCourse = courseRepository.save(newCourse);

        // Save modules directly to the course
        for (ModuleRequestDTO moduleDTO : courseDto.getModules()) {
            Module module = new Module();
            module.setModuleName(moduleDTO.getModuleName());
            module.setCourse(newCourse);
            module = moduleRepository.save(module);

            for (VideoRequestDTO videoDTO : moduleDTO.getVideos()) {
                Video video = new Video();
                video.setFilename(videoDTO.getFilename());
                video.setURL(videoDTO.getURL());
                video.setCloudProvider(cloudUrlProcessorService.detectCloudProvider(videoDTO.getURL()));
                video.setModule(module);
                video.setDescription(videoDTO.getDescription());
                videoRepository.save(video);
            }

            for (DocumentRequestDTO documentDTO : moduleDTO.getDocuments()) {
                Document document = new Document();
                document.setFilename(documentDTO.getFilename());
                document.setURL(documentDTO.getURL());
                document.setCloudProvider(cloudUrlProcessorService.detectCloudProvider(documentDTO.getURL()));
                document.setModule(module);
                documentRepository.save(document);
            }
        }

        log.info("Course created by instructor {}: {}", newCourse.getInstructorId(), newCourse.getTitle());
        return conversion.toResponseDto(newCourse);
    }

    /**
     * Updates the details of an existing course.
     */
    @Transactional
    public CourseResponseDTO updateCourse(Long courseId, CourseUpdateRequest courseDto, Long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructorId().equals(instructorId)) {
            throw new AccessDeniedException("You do not own this course");
        }

        if (courseDto.getTitle() != null) course.setTitle(courseDto.getTitle());
        if (courseDto.getDescription() != null) course.setDescription(courseDto.getDescription());
        if (courseDto.getPrice() != null) course.setPrice(courseDto.getPrice());

        // If modules need to be updated
        if (courseDto.getModules() != null) {
            // Delete existing modules
            List<Module> existingModules = moduleRepository.findByCourseId(courseId);
            for (Module module : existingModules) {
                moduleRepository.delete(module); // cascades to videos and documents
            }

            // Save new modules
            for (ModuleDTO moduleDTO : courseDto.getModules()) {
                Module module = new Module();
                module.setModuleName(moduleDTO.getModuleName());
                module.setCourse(course);
                module = moduleRepository.save(module);

                for (VideoDTO videoDTO : moduleDTO.getVideos()) {
                    Video video = new Video();
                    video.setFilename(videoDTO.getFilename());
                    video.setURL(videoDTO.getURL());
                    video.setCloudProvider(cloudUrlProcessorService.detectCloudProvider(videoDTO.getURL()));
                    video.setModule(module);
                    videoRepository.save(video);
                }

                for (DocumentDTO documentDTO : moduleDTO.getDocuments()) {
                    Document document = new Document();
                    document.setFilename(documentDTO.getFilename());
                    document.setURL(documentDTO.getURL());
                    document.setCloudProvider(cloudUrlProcessorService.detectCloudProvider(documentDTO.getURL()));
                    document.setModule(module);
                    documentRepository.save(document);
                }
            }
        }

        course = courseRepository.save(course);
        log.info("Instructor {} updated course {}", instructorId, courseId);
        return conversion.toResponseDto(course);
    }

    /**
     * Deletes a course by its ID if it exists.
     */
    @Transactional
    public void deleteCourse(Long courseId, Long userId, boolean isAdmin) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Admins can delete any course; instructors can delete only their own
        if (!isAdmin && !course.getInstructorId().equals(userId)) {
            throw new AccessDeniedException("You are not the owner of this course");
        }

        log.info("{} is deleting course {}", isAdmin ? "Admin" : "Instructor", courseId);
        courseRepository.deleteById(courseId);
    }

    /**
     * Retrieves a list of students enrolled in a specific course.
     */
    public List<UserResponseDTO> getEnrolledStudents(Long courseId, Long instructorId) {
        // 1. Check if course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // 2. Check if the instructor is the course owner
        if (!course.getInstructorId().equals(instructorId)) {
            throw new AccessDeniedException("You are not the owner of this course");
        }

        // 3. Get enrollments for the course
        List<Enrollment> enrollments = course.getEnrollmentList();

        // 4. Map each enrollment to a UserDto
        List<UserResponseDTO> enrolledStudents = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            User student = utility.getUserById(enrollment.getStudentId());
            enrolledStudents.add(conversion.toResponseDto(student));
        }

        return enrolledStudents;
    }

    /**
     * Get course content for enrolled students
     */
    public CourseContentResponseDTO getCourseContent(Long courseId, Long studentId) {
        // 1. Validate enrollment
        utility.validateEnrollment(studentId, courseId);

        // 2. Fetch the course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));

        // 3. Log access
        log.info("Returning course content for course ID {} to student ID {}", courseId, studentId);

        // 4. Convert course + modules to CourseContentResponseDTO
        return conversion.toCourseContentResponseDTO(course); // new method you’ll define
    }
    /**
     * Searches for courses by title or description.
     */
    public List<BasicCourseDetailsResponse> searchCourses(String query) {
        List<Course> courses = courseRepository.searchCoursesByTitleOrDescription(query);
        log.info("Found {} courses matching search query '{}'", courses.size(), query);
        return courses.stream().map(conversion::toBasicCourseDto).toList();
    }

    /**
     * Retrieves the most popular courses based on enrollment count.
     */
    public List<BasicCourseDetailsResponse> getPopularCourses(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Course> popularCourses = enrollmentRepository.findMostPopularCourses(pageable);
        log.info("Fetched top {} popular courses", popularCourses.size());
        return popularCourses.stream().map(conversion::toBasicCourseDto).toList();
    }

    /**
     * Retrieves a course entity by its ID. Used as helper method in other services.
     */
    public Course getCourseEntityById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + id));
    }

}