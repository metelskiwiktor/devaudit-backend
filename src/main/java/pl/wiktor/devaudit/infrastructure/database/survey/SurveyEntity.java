package pl.wiktor.devaudit.infrastructure.database.survey;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "surveys")
public class SurveyEntity {
    @Id
    private String id;
    private String mentorId;
    private LocalDateTime creationDate;
    private boolean used;
    
    public SurveyEntity() {
    }
    
    public SurveyEntity(String id, String mentorId, LocalDateTime creationDate, boolean used) {
        this.id = id;
        this.mentorId = mentorId;
        this.creationDate = creationDate;
        this.used = used;
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
    
    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
    
    public boolean getUsed() {
        return used;
    }
    
    public void setUsed(boolean used) {
        this.used = used;
    }
}