package pl.wiktor.devaudit.infrastructure.database.mentor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pl.wiktor.devaudit.domain.mentor.Mentor;
import pl.wiktor.devaudit.domain.mentor.MentorRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MentorRepositoryJPA implements MentorRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(MentorRepositoryJPA.class);

    private final MentorRepositorySpring mentorRepository;

    public MentorRepositoryJPA(MentorRepositorySpring mentorRepository) {
        this.mentorRepository = mentorRepository;
    }

    @Override
    public void save(Mentor mentor) {
        LOGGER.debug("Saving mentor with id: {}", mentor.keycloakId());
        MentorEntity entity = new MentorEntity(mentor.keycloakId(), mentor.firstname(), mentor.email(), mentor.isAdmin());
        mentorRepository.save(entity);
    }

    @Override
    public Optional<Mentor> findById(String id) {
        LOGGER.debug("Finding mentor by id: {}", id);
        return mentorRepository.findById(id)
                .map(entity -> new Mentor(entity.getKeycloakId(), entity.getFirstname(), entity.getEmail(), entity.isAdmin()));
    }

    @Override
    public List<Mentor> findAll() {
        LOGGER.debug("Finding all mentors");
        return mentorRepository.findAll().stream()
                .map(entity -> new Mentor(entity.getKeycloakId(), entity.getFirstname(), entity.getEmail(), entity.isAdmin()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Mentor> findByAdminStatus(boolean isAdmin) {
        LOGGER.debug("Finding mentors with admin status: {}", isAdmin);
        return mentorRepository.findByAdmin(isAdmin).stream()
                .map(entity -> new Mentor(entity.getKeycloakId(), entity.getFirstname(), entity.getEmail(), entity.isAdmin()))
                .collect(Collectors.toList());
    }
}