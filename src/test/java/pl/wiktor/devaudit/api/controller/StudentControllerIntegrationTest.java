package pl.wiktor.devaudit.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import pl.wiktor.devaudit.config.TestSecurityConfig;
import pl.wiktor.devaudit.domain.mentor.Mentor;
import pl.wiktor.devaudit.domain.mentor.MentorRepository;
import pl.wiktor.devaudit.util.JwtTestUtil;
import pl.wiktor.devaudit.infrastructure.database.student.StudentRepositoryJPA;
import pl.wiktor.devaudit.domain.student.Student;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(TestSecurityConfig.class)
class StudentControllerIntegrationTest {
    private static final String API_STUDENT_EXAMPLE = "/api/student/example";
    private static final String STUDENT_ID = "test-id";
    private static final String MENTOR_ROLE = "mentor";
    private static final String STUDENT_ROLE = "student";
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private StudentRepositoryJPA studentRepository;
    @Autowired
    private MentorRepository mentorRepository;

    @BeforeEach
    void setUp() {
        studentRepository.clearDatabase();
    }

    @Test
    void shouldReturnOk_whenStudentIsInJwt_andCreated() {
        //given
        String token = JwtTestUtil.builder()
                .subject(STUDENT_ID)
                .role(STUDENT_ROLE)
                .buildBearer();
        studentRepository.save(new Student(STUDENT_ID, "Jan", "Kowalski"));

        //when //then
        webTestClient.get().uri(API_STUDENT_EXAMPLE)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnUnauthorized_whenStudentIsInJwt_andNotCreated() {
        //given
        String token = JwtTestUtil.builder()
                .subject(STUDENT_ID)
                .role(STUDENT_ROLE)
                .buildBearer();

        //when //then
        webTestClient.get().uri(API_STUDENT_EXAMPLE)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturnForbidden_whenMentorIsInJwt_andCreated() {
        //given
        String token = JwtTestUtil.builder()
                .subject(STUDENT_ID)
                .role(MENTOR_ROLE)
                .buildBearer();
        mentorRepository.save(new Mentor(STUDENT_ID, "Jan", "Kowalski"));
        //when //then
        webTestClient.get().uri(API_STUDENT_EXAMPLE)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus().isForbidden();
    }
}
