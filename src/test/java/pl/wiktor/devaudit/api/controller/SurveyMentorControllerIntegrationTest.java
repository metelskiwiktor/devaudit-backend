package pl.wiktor.devaudit.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import pl.wiktor.devaudit.infrastructure.database.survey.SurveyEntity;
import pl.wiktor.devaudit.domain.survey.SurveyStatus;
import pl.wiktor.devaudit.domain.survey.SurveySubmission;
import pl.wiktor.devaudit.infrastructure.database.survey.SurveyFormEntity;
import pl.wiktor.devaudit.infrastructure.converters.SurveyFormJsonConverter;

import pl.wiktor.devaudit.api.request.GenerateSurveyRequest;
import pl.wiktor.devaudit.config.TestSecurityConfig;
import pl.wiktor.devaudit.domain.mentor.Mentor;
import pl.wiktor.devaudit.domain.mentor.MentorRepository;
import pl.wiktor.devaudit.util.JwtTestUtil;
import pl.wiktor.devaudit.infrastructure.database.mentor.MentorRepositorySpring;
import pl.wiktor.devaudit.infrastructure.database.survey.SurveyRepositorySpring;
import pl.wiktor.devaudit.infrastructure.database.survey.SurveyFormRepositorySpring;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(TestSecurityConfig.class)
class SurveyMentorControllerIntegrationTest {

    private static final String API_GENERATE = "/api/mentor/survey/generate";
    private static final String API_GET_ALL = "/api/mentor/survey/get-all";
    private static final String API_GET_ONE = "/api/mentor/survey/";
    private static final String MENTOR_ID = "mentor-id";
    private static final String MENTOR_ROLE = "mentor";

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private MentorRepository mentorRepository;
    @Autowired
    private MentorRepositorySpring mentorRepositorySpring;
    @Autowired
    private SurveyRepositorySpring surveyRepositorySpring;
    @Autowired
    private SurveyFormRepositorySpring surveyFormRepositorySpring;

    @BeforeEach
    void setUp() {
        mentorRepositorySpring.deleteAll();
        surveyRepositorySpring.deleteAll();
        surveyFormRepositorySpring.deleteAll();
    }

    @Test
    void shouldGenerateSurvey_whenMentorExists() {
        String token = JwtTestUtil.builder()
                .subject(MENTOR_ID)
                .role(MENTOR_ROLE)
                .buildBearer();
        mentorRepository.save(new Mentor(MENTOR_ID, "Jan", "jan@example.com"));
        GenerateSurveyRequest request = new GenerateSurveyRequest("Adam", "Nowak", "adam@test.com");

        webTestClient.post().uri(API_GENERATE)
                .header(HttpHeaders.AUTHORIZATION, token)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.status").isEqualTo("PENDING");
    }

    @Test
    void shouldReturnSurvey_whenMentorRequestsExistingSurvey() {
        String token = JwtTestUtil.builder()
                .subject(MENTOR_ID)
                .role(MENTOR_ROLE)
                .buildBearer();
        mentorRepository.save(new Mentor(MENTOR_ID, "Jan", "jan@example.com"));
        String surveyId = UUID.randomUUID().toString();
        SurveyEntity survey = new SurveyEntity(
                surveyId,
                MENTOR_ID,
                SurveyStatus.COMPLETED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "Adam",
                "Nowak",
                "adam@test.com"
        );
        surveyRepositorySpring.save(survey);

        SurveySubmission submission = new SurveySubmission(
                new SurveySubmission.PersonalInfo("Adam", "Nowak", "adam@test.com", "123", "edu", "gh", "li", "dev", 20, "city", false),
                new SurveySubmission.ProgrammingExperience("junior", "", "", Map.of(), "", "IDE", true, "", ""),
                new SurveySubmission.LearningGoals("goal", "soon", "outcome", List.of("topic"), false, false, "career", "high"),
                new SurveySubmission.LearningPreferences(List.of("style"), Map.of("Mon", "Evening"), "notes", List.of("video"), "email", 1, 60, "weekly", false),
                new SurveySubmission.AdditionalInfo("pc", "none", "notes", "google", "spec", "fast", false, "none")
        );

        SurveyFormEntity form = new SurveyFormEntity();
        form.setSurveyId(surveyId);
        form.setFormData(SurveyFormJsonConverter.convertToJson(submission));
        surveyFormRepositorySpring.save(form);

        webTestClient.get().uri(API_GET_ONE + surveyId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.survey.id").isEqualTo(surveyId)
                .jsonPath("$.surveySubmission.personalInfo.email").isEqualTo("adam@test.com");
    }

    @Test
    void shouldReturnSurveys_whenMentorRequestsAllSurveys() {
        String token = JwtTestUtil.builder()
                .subject(MENTOR_ID)
                .role(MENTOR_ROLE)
                .buildBearer();
        mentorRepository.save(new Mentor(MENTOR_ID, "Jan", "jan@example.com"));
        GenerateSurveyRequest request = new GenerateSurveyRequest("Adam", "Nowak", "adam@test.com");

        webTestClient.post().uri(API_GENERATE)
                .header(HttpHeaders.AUTHORIZATION, token)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get().uri(API_GET_ALL)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").exists();
    }

    @Test
    void shouldReturnUnauthorized_whenMentorNotRegistered_onGetSurvey() {
        String token = JwtTestUtil.builder()
                .subject(MENTOR_ID)
                .role(MENTOR_ROLE)
                .buildBearer();
        // no mentor saved
        webTestClient.get().uri(API_GET_ONE + "non-id")
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturnNotFound_whenSurveyDoesNotExist() {
        String token = JwtTestUtil.builder()
                .subject(MENTOR_ID)
                .role(MENTOR_ROLE)
                .buildBearer();
        mentorRepository.save(new Mentor(MENTOR_ID, "Jan", "jan@example.com"));

        webTestClient.get().uri(API_GET_ONE + "missing")
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus().isNotFound();
    }
}
