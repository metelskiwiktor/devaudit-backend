package pl.wiktor.devaudit.infrastructure.database.mentor;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MentorRepositorySpring extends JpaRepository<MentorEntity, String> {
    List<MentorEntity> findByAdmin(boolean admin);
}