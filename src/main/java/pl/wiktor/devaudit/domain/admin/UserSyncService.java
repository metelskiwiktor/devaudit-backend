// pl.wiktor.devaudit.domain.admin.UserSyncService.java
package pl.wiktor.devaudit.domain.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.wiktor.devaudit.domain.mentor.Mentor;
import pl.wiktor.devaudit.domain.mentor.MentorRepository;
import pl.wiktor.devaudit.domain.student.Student;
import pl.wiktor.devaudit.domain.student.StudentRepository;
import pl.wiktor.devaudit.infrastructure.keycloak.KeycloakClient;
import pl.wiktor.devaudit.infrastructure.keycloak.KeycloakUserDTO;

import java.util.List;
import java.util.Optional;

@Service
public class UserSyncService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserSyncService.class);

    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;
    private final KeycloakClient keycloakClient;

    public UserSyncService(StudentRepository studentRepository,
                           MentorRepository mentorRepository,
                           KeycloakClient keycloakClient) {
        this.studentRepository = studentRepository;
        this.mentorRepository = mentorRepository;
        this.keycloakClient = keycloakClient;
    }

    public int syncUsers() {
        LOGGER.info("Starting user synchronization");
        List<KeycloakUserDTO> keycloakUsers = keycloakClient.getAllUsers();

        int syncedCount = 0;

        for (KeycloakUserDTO keycloakUser : keycloakUsers) {
            try {
                String userId = keycloakUser.id();
                String email = keycloakUser.email();

                if (keycloakUser.isStudent()) {
                    syncedCount += syncStudent(userId, email);
                } else if (keycloakUser.isMentor()) {
                    boolean isAdmin = keycloakUser.isAdmin();
                    syncedCount += syncMentor(userId, email, isAdmin);
                } else {
                    LOGGER.warn("User {} has invalid role combination", userId);
                }
            } catch (Exception e) {
                LOGGER.error("Error syncing user: {}", keycloakUser.id(), e);
            }
        }

        LOGGER.info("User synchronization completed. Synced {} users", syncedCount);
        return syncedCount;
    }

    private int syncStudent(String userId, String email) {
        Optional<Student> existingStudent = studentRepository.findById(userId);

        if (existingStudent.isPresent()) {
            Student student = existingStudent.get();
            if (!student.email().equals(email)) {
                Student updatedStudent = new Student(userId, email);
                LOGGER.info("Updating student: {}", updatedStudent);
                studentRepository.save(updatedStudent);
                return 1;
            }
            return 0;
        } else {
            Student newStudent = new Student(userId, email);
            LOGGER.info("Adding new student: {}", newStudent);
            studentRepository.save(newStudent);
            return 1;
        }
    }

    private int syncMentor(String userId, String email, boolean isAdmin) {
        Optional<Mentor> existingMentor = mentorRepository.findById(userId);

        if (existingMentor.isPresent()) {
            Mentor mentor = existingMentor.get();
            if (!mentor.email().equals(email) || mentor.isAdmin() != isAdmin) {
                LOGGER.info("Updating mentor: {}", mentor);
                Mentor updatedMentor = new Mentor(userId, email, isAdmin);
                mentorRepository.save(updatedMentor);
                return 1;
            }
            return 0;
        } else {
            Mentor newMentor = new Mentor(userId, email, isAdmin);
            LOGGER.info("Adding new mentor: {}", newMentor);
            mentorRepository.save(newMentor);
            return 1;
        }
    }
}