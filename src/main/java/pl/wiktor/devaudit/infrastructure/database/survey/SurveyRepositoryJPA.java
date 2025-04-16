package pl.wiktor.devaudit.infrastructure.database.survey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pl.wiktor.devaudit.domain.survey.Survey;
import pl.wiktor.devaudit.domain.survey.SurveyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SurveyRepositoryJPA implements SurveyRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(SurveyRepositoryJPA.class);

    private final SurveyRepositorySpring surveyRepository;

    public SurveyRepositoryJPA(SurveyRepositorySpring surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    @Override
    public void save(Survey survey) {
        LOGGER.debug("Saving survey with id: {}", survey.id());
        SurveyEntity entity = new SurveyEntity(
                survey.id(),
                survey.mentorId(),
                survey.creationDate(),
                survey.used()
        );
        surveyRepository.save(entity);
    }

    @Override
    public Optional<Survey> findById(String id) {
        LOGGER.debug("Finding survey by id: {}", id);
        return surveyRepository.findById(id)
                .map(entity -> new Survey(
                        entity.getId(),
                        entity.getMentorId(),
                        entity.getCreationDate(),
                        entity.getUsed()
                ));
    }

    @Override
    public List<Survey> findAllByMentorId(String mentorId) {
        LOGGER.debug("Finding all surveys by mentor ID: {}", mentorId);
        return surveyRepository.findByMentorId(mentorId).stream()
                .map(entity -> new Survey(
                        entity.getId(),
                        entity.getMentorId(),
                        entity.getCreationDate(),
                        entity.getUsed()
                ))
                .collect(Collectors.toList());
    }
}