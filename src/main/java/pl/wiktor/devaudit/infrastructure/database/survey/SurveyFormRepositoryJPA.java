package pl.wiktor.devaudit.infrastructure.database.survey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pl.wiktor.devaudit.domain.survey.SurveyFormRepository;
import pl.wiktor.devaudit.domain.survey.SurveySubmission;
import pl.wiktor.devaudit.infrastructure.converters.SurveyFormJsonConverter;

import java.util.Optional;

@Repository
public class SurveyFormRepositoryJPA implements SurveyFormRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(SurveyFormRepositoryJPA.class);

    private final SurveyFormRepositorySpring surveyFormRepository;

    public SurveyFormRepositoryJPA(SurveyFormRepositorySpring surveyFormRepository) {
        this.surveyFormRepository = surveyFormRepository;
    }

    @Override
    public void saveSurveyForm(SurveySubmission surveySubmission, String surveyId) {
        LOGGER.debug("Saving survey form for survey ID: {}", surveyId);
        SurveyFormEntity entity = mapToEntity(surveySubmission, surveyId);
        surveyFormRepository.save(entity);
    }

    @Override
    public Optional<SurveySubmission> findBySurveyId(String surveyId) {
        LOGGER.debug("Finding survey form by survey ID: {}", surveyId);
        return surveyFormRepository.findBySurveyId(surveyId)
                .map(this::mapToDomain);
    }

    private SurveyFormEntity mapToEntity(SurveySubmission submission, String surveyId) {
        SurveyFormEntity entity = new SurveyFormEntity();
        entity.setSurveyId(surveyId);
        
        // Konwersja danych do formatu JSON
        entity.setFormData(SurveyFormJsonConverter.convertToJson(submission));
        
        return entity;
    }

    private SurveySubmission mapToDomain(SurveyFormEntity entity) {
        return SurveyFormJsonConverter.convertFromJson(entity.getFormData());
    }
}