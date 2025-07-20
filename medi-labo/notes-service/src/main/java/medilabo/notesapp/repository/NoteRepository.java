package medilabo.notesapp.repository;

import medilabo.notesapp.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The interface used to perform CRUD operations on the database
 */
@Repository
public interface NoteRepository extends MongoRepository<Note, String> {

    List<Note> findByPatientId(int patientId);
}
