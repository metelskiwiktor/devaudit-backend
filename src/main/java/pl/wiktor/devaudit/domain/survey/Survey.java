package pl.wiktor.devaudit.domain.survey;

import pl.wiktor.devaudit.domain.mentor.Mentor;

import java.time.LocalDateTime;
import java.util.UUID;

public record Survey(
        String id,
        Mentor mentor,
        SurveyStatus status,
        LocalDateTime creationDate,
        LocalDateTime completedDate,
        SurveyStudentInfo studentInfo
) {
    public static Survey create(Mentor mentor, SurveyStudentInfo studentInfo) {
        return new Survey(
                UUID.randomUUID().toString(),
                mentor,
                SurveyStatus.PENDING,
                LocalDateTime.now(),
                null,
                studentInfo
        );
    }

    public Survey markAsCompleted(SurveyStudentInfo studentInfo) {
        return new Survey(id, mentor, SurveyStatus.COMPLETED, creationDate, LocalDateTime.now(), studentInfo);
    }

    public Survey abort() {
        return new Survey(id, mentor, SurveyStatus.ABORTED, creationDate, LocalDateTime.now(), studentInfo);
    }
}