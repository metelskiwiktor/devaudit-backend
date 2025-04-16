package pl.wiktor.devaudit.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.wiktor.devaudit.api.response.GenerateSurveyResponse;
import pl.wiktor.devaudit.domain.mentor.Mentor;
import pl.wiktor.devaudit.domain.survey.Survey;
import pl.wiktor.devaudit.domain.survey.SurveyService;
import pl.wiktor.devaudit.infrastructure.security.LoggedMentor;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mentor")
public class MentorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MentorController.class);

    private final SurveyService surveyService;

    public MentorController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @GetMapping("/example")
    public void example(@LoggedMentor Mentor user) {
        LOGGER.info("Example method called by mentor: {}", user);
    }

    @PostMapping("/survey/generate")
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

    @GetMapping("/surveys")
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