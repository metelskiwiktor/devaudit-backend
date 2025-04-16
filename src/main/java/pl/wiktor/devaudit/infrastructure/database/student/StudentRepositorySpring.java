package pl.wiktor.devaudit.infrastructure.database.student;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepositorySpring extends JpaRepository<StudentEntity, String> {
}
