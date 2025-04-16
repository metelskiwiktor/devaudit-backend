package pl.wiktor.devaudit.domain.survey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.wiktor.devaudit.domain.exception.SurveyAlreadyUsedException;
import pl.wiktor.devaudit.domain.exception.SurveyNotFoundException;
import pl.wiktor.devaudit.domain.student.Student;
import pl.wiktor.devaudit.domain.student.StudentRepository;
import pl.wiktor.devaudit.infrastructure.keycloak.KeycloakRegistrationService;

import java.util.List;

@Service
public class SurveyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SurveyService.class);

    private final SurveyRepository surveyRepository;
    private final StudentRepository studentRepository;
    private final KeycloakRegistrationService keycloakRegistrationService;

    public SurveyService(SurveyRepository surveyRepository,
                         StudentRepository studentRepository,
                         KeycloakRegistrationService keycloakRegistrationService) {
        this.surveyRepository = surveyRepository;
        this.studentRepository = studentRepository;
        this.keycloakRegistrationService = keycloakRegistrationService;
    }

    public Survey generateSurvey(String mentorId) {
        LOGGER.info("Generating survey for mentor: {}", mentorId);
        Survey survey = Survey.create(mentorId);
        surveyRepository.save(survey);
        LOGGER.info("Survey generated with UUID: {}", survey.id());
        return survey;
    }

    public List<Survey> getSurveysByMentorId(String mentorId) {
        LOGGER.info("Getting surveys for mentor: {}", mentorId);
        return surveyRepository.findAllByMentorId(mentorId);
    }

    public void submitSurvey(String surveyId, String firstName, String lastName, String email) {
        LOGGER.info("Processing survey submission for UUID: {}", surveyId);

        Validator.validateSubmitSurvey(surveyId, firstName, lastName, email);

        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> {
                    LOGGER.warn("Survey with UUID {} not found", surveyId);
                    return new SurveyNotFoundException(surveyId, HttpStatus.NOT_FOUND);
                });

        if (survey.used()) {
            LOGGER.warn("Survey with UUID {} already used", surveyId);
            throw new SurveyAlreadyUsedException(surveyId, HttpStatus.BAD_REQUEST);
        }

        // Register student in Keycloak
        String keycloakId = keycloakRegistrationService.registerStudent(firstName, lastName, email);
        LOGGER.info("Student registered in Keycloak with ID: {}", keycloakId);

        // Save student in database
        Student student = new Student(keycloakId, email);
        studentRepository.save(student);
        LOGGER.info("Student saved in database");

        // Mark survey as used
        Survey updatedSurvey = survey.markAsUsed();
        surveyRepository.save(updatedSurvey);
        LOGGER.info("Survey marked as used");
    }

    static class Validator {
        public static void validateSubmitSurvey(String surveyId, String firstName, String lastName, String email) {
            if (surveyId == null || surveyId.isEmpty()) {
                throw new IllegalArgumentException("Survey ID cannot be null or empty");
            }
            if (firstName == null || firstName.isEmpty()) {
                throw new IllegalArgumentException("First name cannot be null or empty");
            }
            if (lastName == null || lastName.isEmpty()) {
                throw new IllegalArgumentException("Last name cannot be null or empty");
            }
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Email cannot be null or empty");
            }
        }
    }
}