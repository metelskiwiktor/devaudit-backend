package pl.wiktor.devaudit.infrastructure.database.student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pl.wiktor.devaudit.domain.student.Student;
import pl.wiktor.devaudit.domain.student.StudentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class StudentRepositoryJPA implements StudentRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(StudentRepositoryJPA.class);

    private final StudentRepositorySpring studentRepository;

    public StudentRepositoryJPA(StudentRepositorySpring studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void save(Student student) {
        LOGGER.debug("Saving student with id: {}", student.keycloakId());
        StudentEntity entity = new StudentEntity(student.keycloakId(), student.email());
        studentRepository.save(entity);
    }

    @Override
    public Optional<Student> findById(String id) {
        LOGGER.debug("Finding student by id: {}", id);
        return studentRepository.findById(id)
                .map(entity -> new Student(entity.getKeycloakId(), entity.getEmail()));
    }

    @Override
    public List<Student> findAll() {
        LOGGER.debug("Finding all students");
        return studentRepository.findAll().stream()
                .map(entity -> new Student(entity.getKeycloakId(), entity.getEmail()))
                .collect(Collectors.toList());
    }
}