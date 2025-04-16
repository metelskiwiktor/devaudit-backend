package pl.wiktor.devaudit.domain.survey;

import java.time.LocalDateTime;
import java.util.UUID;

public record Survey(
        String id,
        String mentorId,
        LocalDateTime creationDate,
        boolean used
) {
    public static Survey create(String mentorId) {
        return new Survey(
                UUID.randomUUID().toString(),
                mentorId,
                LocalDateTime.now(),
                false
        );
    }

    public Survey markAsUsed() {
        return new Survey(id, mentorId, creationDate, true);
    }
}