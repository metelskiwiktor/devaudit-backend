package pl.wiktor.devaudit.api.response;

import java.time.LocalDateTime;

public record GenerateSurveyResponse(
        String id,
        LocalDateTime creationDate,
        boolean used
) {
}