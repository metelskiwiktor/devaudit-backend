package pl.wiktor.devaudit.infrastructure.database.survey;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import pl.wiktor.devaudit.domain.survey.SurveyStatus;

@Entity
@Table(name = "surveys")
public class SurveyEntity {
    @Id
    private String id;
    private String mentorId;
    @Enumerated(EnumType.STRING)
    private SurveyStatus status;
    private LocalDateTime creationDate;
    private LocalDateTime completedDate;
    private String studentFirstName;
    private String studentLastName;
    private String studentEmail;
    
    public SurveyEntity() {
    }
    
    public SurveyEntity(String id,
                        String mentorId,
                        SurveyStatus status,
                        LocalDateTime creationDate,
                        LocalDateTime completedDate,
                        String studentFirstName,
                        String studentLastName,
                        String studentEmail) {
        this.id = id;
        this.mentorId = mentorId;
        this.status = status;
        this.creationDate = creationDate;
        this.completedDate = completedDate;
        this.studentFirstName = studentFirstName;
        this.studentLastName = studentLastName;
        this.studentEmail = studentEmail;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String uuid) {
        this.id = uuid;
    }
    
    public String getMentorId() {
        return mentorId;
    }

    public void setMentorId(String mentorId) {
        this.mentorId = mentorId;
    }

    public SurveyStatus getStatus() {
        return status;
    }

    public void setStatus(SurveyStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }

    public String getStudentFirstName() {
        return studentFirstName;
    }

    public void setStudentFirstName(String studentFirstName) {
        this.studentFirstName = studentFirstName;
    }

    public String getStudentLastName() {
        return studentLastName;
    }

    public void setStudentLastName(String studentLastName) {
        this.studentLastName = studentLastName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }
}