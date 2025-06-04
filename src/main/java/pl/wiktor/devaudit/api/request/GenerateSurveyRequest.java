package pl.wiktor.devaudit.api.request;

public record GenerateSurveyRequest(
        String firstName,
        String lastName,
        String email
) {}
