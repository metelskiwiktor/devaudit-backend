package pl.wiktor.devaudit.domain.mentor;

import java.util.List;
import java.util.Optional;

public interface MentorRepository {
    void save(Mentor mentor);
    Optional<Mentor> findById(String id);
    List<Mentor> findAll();
    List<Mentor> findByAdminStatus(boolean isAdmin);
}