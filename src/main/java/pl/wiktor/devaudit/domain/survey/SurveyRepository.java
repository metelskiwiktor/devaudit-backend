package pl.wiktor.devaudit.domain.survey;

import java.util.List;
import java.util.Optional;

public interface SurveyRepository {
    void save(Survey survey);
    Optional<Survey> findById(String id);
    List<Survey> findAllByMentorId(String mentorId);
}