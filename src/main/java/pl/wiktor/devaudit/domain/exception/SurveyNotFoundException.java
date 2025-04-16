package pl.wiktor.devaudit.domain.exception;

import org.springframework.http.HttpStatus;

public class SurveyNotFoundException extends DomainException {
    private static final String MESSAGE_TEMPLATE = "Survey with id '%s' not found";

    public SurveyNotFoundException(String id, HttpStatus status) {
        super(MESSAGE_TEMPLATE.formatted(id), status);
    }
}