package pl.wiktor.devaudit.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.wiktor.devaudit.api.request.GenerateSurveyRequest;
import pl.wiktor.devaudit.api.response.GenerateSurveyResponse;
import pl.wiktor.devaudit.api.response.SurveyResponse;
import pl.wiktor.devaudit.domain.mentor.Mentor;
import pl.wiktor.devaudit.domain.survey.Survey;
import pl.wiktor.devaudit.domain.survey.SurveyService;
import pl.wiktor.devaudit.domain.survey.SurveyStudentInfo;
import pl.wiktor.devaudit.infrastructure.security.annotation.LoggedMentor;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mentor/survey")
public class SurveyMentorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SurveyMentorController.class);
    private final SurveyService surveyService;

    public SurveyMentorController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @PostMapping("/generate")
    public ResponseEntity<GenerateSurveyResponse> generateSurvey(@LoggedMentor Mentor mentor,
                                                                 @RequestBody GenerateSurveyRequest request) {
        LOGGER.info("Generate survey called by mentor: {}", mentor.email());

        SurveyStudentInfo studentInfo = new SurveyStudentInfo(request.firstName(), request.lastName(), request.email());
        Survey survey = surveyService.generateSurvey(mentor, studentInfo);

        GenerateSurveyResponse response = new GenerateSurveyResponse(
                survey.id(),
                survey.creationDate(),
                survey.status()
        );

        LOGGER.debug("Returning survey response: {}", response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{surveyId}")
    public ResponseEntity<SurveyResponse> getSurvey(@LoggedMentor Mentor mentor, @PathVariable String surveyId) {
        LOGGER.info("Checking survey ID: {} by mentor: {}", surveyId, mentor.email());

        SurveyResponse response = new SurveyResponse(
                surveyService.getSurvey(surveyId),
                surveyService.getSurveySubmission(surveyId)
        );

        LOGGER.debug("Returning survey response: {}", response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<GenerateSurveyResponse>> getSurveys(@LoggedMentor Mentor mentor) {
        LOGGER.info("Get surveys called by mentor: {}", mentor.email());

        List<Survey> surveys = surveyService.getSurveysByMentorId(mentor.keycloakId());

        List<GenerateSurveyResponse> response = surveys.stream()
                .map(survey -> new GenerateSurveyResponse(
                        survey.id(),
                        survey.creationDate(),
                        survey.status()
                ))
                .collect(Collectors.toList());

        LOGGER.debug("Returning {} surveys", response.size());

        return ResponseEntity.ok(response);
    }
}
