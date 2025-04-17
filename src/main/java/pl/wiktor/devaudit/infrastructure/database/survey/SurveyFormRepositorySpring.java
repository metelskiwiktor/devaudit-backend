package pl.wiktor.devaudit.infrastructure.database.survey;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SurveyFormRepositorySpring extends JpaRepository<SurveyFormEntity, String> {
    Optional<SurveyFormEntity> findBySurveyId(String surveyId);
}