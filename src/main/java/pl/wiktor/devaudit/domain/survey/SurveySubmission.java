package pl.wiktor.devaudit.domain.survey;

import java.util.List;
import java.util.Map;

public record SurveySubmission(
        PersonalInfo personalInfo,
        ProgrammingExperience programmingExperience,
        LearningGoals learningGoals,
        LearningPreferences learningPreferences,
        AdditionalInfo additionalInfo
) {
    public record PersonalInfo(
            String firstName,
            String lastName,
            String email,
            String phone,
            String education,
            String githubUrl,
            String linkedinUrl,
            String currentOccupation,
            int age,
            String city,
            boolean remoteOnly
    ) {
    }

    public record ProgrammingExperience(
            String javaExperience,
            String otherLanguages,
            String priorProjects,
            Map<String, String> skillLevels,
            String previousCourses,
            String preferredIDE,
            boolean hasUsedGit,
            String databaseExperience,
            String buildToolsExperience
    ) {
    }

    public record LearningGoals(
            String mainGoal,
            String timeline,
            String expectedOutcome,
            List<String> specificTopics,
            boolean wantsIndustryProject,
            boolean needsCertification,
            String careerPlans,
            String motivationLevel
    ) {
    }

    public record LearningPreferences(
            List<String> learningStyle,
            Map<String, String> availabilityByDay,
            String availabilityNotes,
            List<String> materialTypes,
            String preferredCommunication,
            int sessionsPerWeek,
            int preferredSessionLength,
            String feedbackFrequency,
            boolean needsFlexibleSchedule
    ) {
    }

    public record AdditionalInfo(
            String availableTools,
            String difficulties,
            String additionalNotes,
            String howDidYouFindUs,
            String computerSpecs,
            String internetSpeed,
            boolean specialAccommodations,
            String accommodationDetails
    ) {
    }
}