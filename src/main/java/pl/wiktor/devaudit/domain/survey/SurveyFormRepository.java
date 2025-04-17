package pl.wiktor.devaudit.domain.survey;

import java.util.Optional;

public interface SurveyFormRepository {
    void saveSurveyForm(SurveySubmission surveySubmission, String surveyId);
    Optional<SurveySubmission> findBySurveyId(String surveyId);
}