package pl.wiktor.devaudit.domain.survey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.wiktor.devaudit.domain.exception.SurveyAlreadyUsedException;
import pl.wiktor.devaudit.domain.exception.SurveyNotFoundException;
import pl.wiktor.devaudit.domain.student.Student;
import pl.wiktor.devaudit.domain.student.StudentRepository;
import pl.wiktor.devaudit.infrastructure.keycloak.KeycloakRegistrationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SurveyServiceTest {

    @Mock
    private SurveyRepository surveyRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private KeycloakRegistrationService keycloakRegistrationService;

    private SurveyService surveyService;

    @BeforeEach
    void setUp() {
        surveyService = new SurveyService(surveyRepository, studentRepository, keycloakRegistrationService);
    }

    @Test
    void shouldGenerateSurvey() {
        // Given
        String mentorId = UUID.randomUUID().toString();

        // When
        Survey generatedSurvey = surveyService.generateSurvey(mentorId);

        // Then
        assertNotNull(generatedSurvey);
        assertEquals(mentorId, generatedSurvey.mentorId());
        assertFalse(generatedSurvey.used());
        assertNotNull(generatedSurvey.id());
        assertNotNull(generatedSurvey.creationDate());

        verify(surveyRepository).save(any(Survey.class));
    }

    @Test
    void shouldGetSurveysByMentorId() {
        // Given
        String mentorId = UUID.randomUUID().toString();
        Survey survey1 = new Survey(UUID.randomUUID().toString(), mentorId, LocalDateTime.now(), false);
        Survey survey2 = new Survey(UUID.randomUUID().toString(), mentorId, LocalDateTime.now(), true);
        when(surveyRepository.findAllByMentorId(mentorId)).thenReturn(List.of(survey1, survey2));

        // When
        List<Survey> surveys = surveyService.getSurveysByMentorId(mentorId);

        // Then
        assertEquals(2, surveys.size());
        assertTrue(surveys.contains(survey1));
        assertTrue(surveys.contains(survey2));

        verify(surveyRepository).findAllByMentorId(mentorId);
    }

    @Test
    void shouldSubmitSurvey() {
        // Given
        String surveyUuid = UUID.randomUUID().toString();
        String mentorId = UUID.randomUUID().toString();
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@example.com";
        String keycloakId = UUID.randomUUID().toString();

        Survey survey = new Survey(surveyUuid, mentorId, LocalDateTime.now(), false);
        when(surveyRepository.findById(surveyUuid)).thenReturn(Optional.of(survey));
        when(keycloakRegistrationService.registerStudent(firstName, lastName, email)).thenReturn(keycloakId);

        // When
        surveyService.submitSurvey(surveyUuid, firstName, lastName, email);

        // Then
        verify(surveyRepository).findById(surveyUuid);
        verify(keycloakRegistrationService).registerStudent(firstName, lastName, email);
        verify(studentRepository).save(any(Student.class));
        verify(surveyRepository).save(any(Survey.class));
    }

    @Test
    void shouldThrowExceptionWhenSurveyNotFound() {
        // Given
        String surveyUuid = UUID.randomUUID().toString();
        when(surveyRepository.findById(surveyUuid)).thenReturn(Optional.empty());

        // When, Then
        Exception exception = assertThrows(SurveyNotFoundException.class, () ->
                surveyService.submitSurvey(surveyUuid, "John", "Doe", "john.doe@example.com")
        );

        assertEquals("Survey with id '" + surveyUuid + "' not found", exception.getMessage());
        verify(surveyRepository).findById(surveyUuid);
        verify(keycloakRegistrationService, never()).registerStudent(anyString(), anyString(), anyString());
        verify(studentRepository, never()).save(any(Student.class));
        verify(surveyRepository, never()).save(any(Survey.class));
    }

    @Test
    void shouldThrowExceptionWhenSurveyAlreadyUsed() {
        // Given
        String surveyUuid = UUID.randomUUID().toString();
        String mentorId = UUID.randomUUID().toString();

        Survey survey = new Survey(surveyUuid, mentorId, LocalDateTime.now(), true);
        when(surveyRepository.findById(surveyUuid)).thenReturn(Optional.of(survey));

        // When, Then
        Exception exception = assertThrows(SurveyAlreadyUsedException.class, () ->
                surveyService.submitSurvey(surveyUuid, "John", "Doe", "john.doe@example.com")
        );

        assertEquals("Survey with id '" + surveyUuid + "' has already been used", exception.getMessage());
        verify(surveyRepository).findById(surveyUuid);
        verify(keycloakRegistrationService, never()).registerStudent(anyString(), anyString(), anyString());
        verify(studentRepository, never()).save(any(Student.class));
        verify(surveyRepository, never()).save(any(Survey.class));
    }
}