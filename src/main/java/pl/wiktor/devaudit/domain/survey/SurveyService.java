package pl.wiktor.devaudit.domain.survey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.wiktor.devaudit.domain.exception.SurveyAlreadyUsedException;
import pl.wiktor.devaudit.domain.exception.SurveyNotFoundException;
import pl.wiktor.devaudit.domain.mentor.Mentor;
import pl.wiktor.devaudit.domain.student.Student;
import pl.wiktor.devaudit.domain.student.StudentRepository;
import pl.wiktor.devaudit.infrastructure.keycloak.KeycloakRegistrationService;
import pl.wiktor.devaudit.domain.survey.SurveyStatus;
import pl.wiktor.devaudit.domain.survey.SurveyStudentInfo;

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

    public Survey generateSurvey(Mentor mentor, SurveyStudentInfo studentInfo) {
        LOGGER.info("Generating survey for mentor: {}", mentor.keycloakId());
        Survey survey = Survey.create(mentor, studentInfo);
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

        if (survey.status() != SurveyStatus.PENDING) {
            LOGGER.warn("Survey {} already completed", surveyId);
            throw new SurveyAlreadyUsedException(surveyId, HttpStatus.BAD_REQUEST);

        }

        String email = surveySubmission.personalInfo().email();
        String firstName = surveySubmission.personalInfo().firstName();
        String lastName = surveySubmission.personalInfo().lastName();

        surveyFormRepository.saveSurveyForm(surveySubmission, surveyId);
        LOGGER.info("Survey form data saved successfully");

        SurveyStudentInfo studentInfo = new SurveyStudentInfo(firstName, lastName, email);
        Survey updatedSurvey = survey.markAsCompleted(studentInfo);
        surveyRepository.save(updatedSurvey);
        LOGGER.debug("Survey marked as completed");

        String keycloakId = keycloakRegistrationService.registerStudent(firstName, lastName, email);
        LOGGER.info("Student registered in Keycloak with ID: {}", keycloakId);

        Student student = new Student(keycloakId, firstName, email);
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