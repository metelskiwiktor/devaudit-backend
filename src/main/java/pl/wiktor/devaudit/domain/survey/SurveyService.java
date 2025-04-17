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
    private final SurveyFormRepository surveyFormRepository;

    public SurveyService(SurveyRepository surveyRepository,
                         StudentRepository studentRepository,
                         KeycloakRegistrationService keycloakRegistrationService,
                         SurveyFormRepository surveyFormRepository) {
        this.surveyRepository = surveyRepository;
        this.studentRepository = studentRepository;
        this.keycloakRegistrationService = keycloakRegistrationService;
        this.surveyFormRepository = surveyFormRepository;
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

    public void submitSurveyForm(SurveySubmission surveySubmission, String surveyId) {
        LOGGER.info("Processing detailed survey form for survey ID: {}", surveyId);

        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> {
                    LOGGER.warn("Survey with UUID {} not found", surveyId);
                    return new SurveyNotFoundException(surveyId, HttpStatus.NOT_FOUND);
                });

        if (survey.used()) {
            LOGGER.warn("Survey {} already used", surveyId);
            throw new SurveyAlreadyUsedException(surveyId, HttpStatus.BAD_REQUEST);

        }

        // Save the survey form data
        surveyFormRepository.saveSurveyForm(surveySubmission, surveyId);
        LOGGER.info("Survey form data saved successfully");

        // Mark survey as used if not already used
        Survey updatedSurvey = survey.markAsUsed();
        surveyRepository.save(updatedSurvey);
        LOGGER.debug("Survey marked as used");

        // Extract basic info for Keycloak registration
        String fullName = surveySubmission.personalInfo().fullName();
        String email = surveySubmission.personalInfo().email();

        // Split fullName into firstName and lastName
        String[] nameParts = fullName.split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        // Register student in Keycloak
        String keycloakId = keycloakRegistrationService.registerStudent(firstName, lastName, email);
        LOGGER.info("Student registered in Keycloak with ID: {}", keycloakId);

        // Save student in database
        Student student = new Student(keycloakId, email);
        studentRepository.save(student);
        LOGGER.info("Student saved in database");
    }

    public SurveySubmission getSurveySubmission(String surveyId) {
        LOGGER.info("Getting survey form for survey ID: {}", surveyId);
        return surveyFormRepository.findBySurveyId(surveyId)
                .orElseThrow(() -> new SurveyNotFoundException(surveyId, HttpStatus.NOT_FOUND));
    }

    public Survey getSurvey(String surveyId) {
        LOGGER.info("Getting survey ID: {}", surveyId);
        return surveyRepository.findById(surveyId)
                .orElseThrow(() -> new SurveyNotFoundException(surveyId, HttpStatus.NOT_FOUND));
    }
}