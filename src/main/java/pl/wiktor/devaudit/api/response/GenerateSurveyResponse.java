package pl.wiktor.devaudit.api.response;

import java.time.LocalDateTime;
import pl.wiktor.devaudit.domain.survey.SurveyStatus;

public record GenerateSurveyResponse(
        String id,
        LocalDateTime creationDate,
        SurveyStatus status
) {
}