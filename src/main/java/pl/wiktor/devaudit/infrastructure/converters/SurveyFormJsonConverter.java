package pl.wiktor.devaudit.infrastructure.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.wiktor.devaudit.domain.survey.SurveySubmission;

public class SurveyFormJsonConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SurveyFormJsonConverter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String convertToJson(SurveySubmission submission) {
        try {
            return objectMapper.writeValueAsString(submission);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error converting survey submission to JSON", e);
            throw new RuntimeException("Error converting survey submission to JSON", e);
        }
    }

    public static SurveySubmission convertFromJson(String json) {
        try {
            return objectMapper.readValue(json, SurveySubmission.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error converting JSON to survey submission", e);
            throw new RuntimeException("Error converting JSON to survey submission", e);
        }
    }
}