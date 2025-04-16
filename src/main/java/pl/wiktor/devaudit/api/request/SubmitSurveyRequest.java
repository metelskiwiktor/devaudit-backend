package pl.wiktor.devaudit.api.request;

public record SubmitSurveyRequest(
        String firstName,
        String lastName,
        String email
) {
}