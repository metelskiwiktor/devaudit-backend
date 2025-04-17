package pl.wiktor.devaudit.api.response;

import pl.wiktor.devaudit.domain.survey.Survey;
import pl.wiktor.devaudit.domain.survey.SurveySubmission;

public record SurveyResponse(Survey survey, SurveySubmission surveySubmission) {}
