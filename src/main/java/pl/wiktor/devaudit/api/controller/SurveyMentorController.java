package pl.wiktor.devaudit.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.wiktor.devaudit.api.response.GenerateSurveyResponse;
import pl.wiktor.devaudit.api.response.SurveyResponse;
import pl.wiktor.devaudit.domain.mentor.Mentor;
import pl.wiktor.devaudit.domain.survey.Survey;
import pl.wiktor.devaudit.domain.survey.SurveyService;
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
    public ResponseEntity<GenerateSurveyResponse> generateSurvey(@LoggedMentor Mentor mentor) {
        LOGGER.info("Generate survey called by mentor: {}", mentor.keycloakId());

        Survey survey = surveyService.generateSurvey(mentor.keycloakId());

        GenerateSurveyResponse response = new GenerateSurveyResponse(
                survey.id(),
                survey.creationDate(),
                survey.used()
        );

        return ResponseEntity.ok(response);
    }

@GetMapping("/{surveyId}")
        public ResponseEntity<SurveyResponse> getSurvey(@PathVariable String surveyId) {
            LOGGER.info("Checking survey ID: {}", surveyId);

            SurveyResponse response = new SurveyResponse(
                    surveyService.getSurvey(surveyId),
                    surveyService.getSurveySubmission(surveyId)
            );

            return ResponseEntity.ok(response);
        }

    @GetMapping("/get-all")
    public ResponseEntity<List<GenerateSurveyResponse>> getSurveys(@LoggedMentor Mentor mentor) {
        LOGGER.info("Get surveys called by mentor: {}", mentor.keycloakId());

        List<Survey> surveys = surveyService.getSurveysByMentorId(mentor.keycloakId());

        List<GenerateSurveyResponse> response = surveys.stream()
                .map(survey -> new GenerateSurveyResponse(
                        survey.id(),
                        survey.creationDate(),
                        survey.used()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
