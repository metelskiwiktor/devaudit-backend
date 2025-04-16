package pl.wiktor.devaudit.infrastructure.database.survey;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SurveyRepositorySpring extends JpaRepository<SurveyEntity, String> {
    List<SurveyEntity> findByMentorId(String mentorId);
}