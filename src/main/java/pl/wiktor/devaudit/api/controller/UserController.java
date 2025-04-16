package pl.wiktor.devaudit.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.wiktor.devaudit.api.request.SubmitSurveyRequest;
import pl.wiktor.devaudit.domain.survey.SurveyService;

/**
 * UserController is responsible for managing not register/logged operations.
 * Can be accessed by anyone.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final SurveyService surveyService;

    public UserController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @PostMapping("/survey/{uuid}/submit")
    public ResponseEntity<Void> submitSurvey(@PathVariable String uuid, @RequestBody SubmitSurveyRequest request) {
        LOGGER.info("Submitting survey for UUID: {}", uuid);

        surveyService.submitSurvey(
                uuid,
                request.firstName(),
                request.lastName(),
                request.email()
        );

        return ResponseEntity.ok().build();
    }
}