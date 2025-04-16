package pl.wiktor.devaudit.domain.student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    void save(Student student);
    Optional<Student> findById(String id);
    List<Student> findAll();
}