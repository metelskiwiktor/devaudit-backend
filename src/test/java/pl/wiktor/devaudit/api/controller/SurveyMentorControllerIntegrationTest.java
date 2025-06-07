package pl.wiktor.devaudit.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.wiktor.devaudit.domain.mentor.Mentor;
import pl.wiktor.devaudit.domain.mentor.MentorRepository;
import pl.wiktor.devaudit.domain.survey.Survey;
import pl.wiktor.devaudit.domain.survey.SurveyRepository;
import pl.wiktor.devaudit.domain.survey.SurveyStatus;

import static org.assertj.core.api.Assertions.assertThat;
import pl.wiktor.devaudit.api.controller.ContainersConfig;
import pl.wiktor.devaudit.api.config.KeycloakTestConfig;
import pl.wiktor.devaudit.api.config.KeycloakTestConfig.KeycloakTokenProvider;

import pl.wiktor.devaudit.DevauditBackendApplication;

@SpringBootTest(classes = DevauditBackendApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ContextConfiguration(classes = {ContainersConfig.class, KeycloakTestConfig.class})
class SurveyMentorControllerIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    MentorRepository mentorRepository;

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KeycloakTokenProvider tokenProvider;

    private static final String MENTOR_ID = "mentor-1";

    @BeforeEach
    void setup() {
        mentorRepository.save(new Mentor(MENTOR_ID, "John", "john@example.com", false));
    }

    @Test
    void generateSurveyStoresStudentInfo() throws Exception {
        // given
        String body = objectMapper.writeValueAsString(
                new GenerateSurveyRequest("Alice", "Smith", "alice@example.com")
        );

        // when
        var result = webTestClient.post()
                .uri("/api/mentor/survey/generate")
                .headers(headers -> headers.setBearerAuth(tokenProvider.obtainToken("mentor", "password")))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        String id = JsonPath.read(result.getResponseBody(), "$.id");

        // then
        Survey survey = surveyRepository.findById(id).orElseThrow();
        assertThat(survey.status()).isEqualTo(SurveyStatus.PENDING);
        assertThat(survey.studentInfo().firstName()).isEqualTo("Alice");
        assertThat(survey.completedDate()).isNull();
    }

    private record GenerateSurveyRequest(String firstName, String lastName, String email) {}
}
