package pl.wiktor.devaudit.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.wiktor.devaudit.api.request.SubmitSurveyFormRequest;
import pl.wiktor.devaudit.domain.survey.SurveyService;
import pl.wiktor.devaudit.domain.survey.SurveySubmission;

/**
 * AnonymousController is responsible for managing not register/logged operations.
 * Can be accessed by anyone.
 */
@RestController
@RequestMapping("/api/anonymous")
public class AnonymousController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnonymousController.class);

    private final SurveyService surveyService;
    private final ConversionService conversionService;

    public AnonymousController(SurveyService surveyService, ConversionService conversionService) {
        this.surveyService = surveyService;
        this.conversionService = conversionService;
    }

    @PostMapping("/survey/{surveyId}/submit")
    public ResponseEntity<Void> submitSurvey(@PathVariable String surveyId, @RequestBody SubmitSurveyFormRequest request) {
        LOGGER.info("Submitting survey for id: {}", surveyId);

        surveyService.submitSurveyForm(conversionService.convert(request, SurveySubmission.class), surveyId);

        return ResponseEntity.ok().build();
    }
}