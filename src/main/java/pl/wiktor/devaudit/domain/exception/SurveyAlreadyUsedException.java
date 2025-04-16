package pl.wiktor.devaudit.domain.exception;

import org.springframework.http.HttpStatus;

public class SurveyAlreadyUsedException extends DomainException {
    private static final String MESSAGE_TEMPLATE = "Survey with id '%s' has already been used";

    public SurveyAlreadyUsedException(String id, HttpStatus status) {
        super(MESSAGE_TEMPLATE.formatted(id), status);
    }
}