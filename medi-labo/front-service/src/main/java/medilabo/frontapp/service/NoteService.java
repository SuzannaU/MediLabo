package medilabo.frontapp.service;

import feign.FeignException;
import medilabo.frontapp.model.Note;
import medilabo.frontapp.proxy.NoteProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service used to handle Response entities received from the proxy interface, regarding Note objects.
 *
 * @see NoteProxy
 */
@Service
public class NoteService {
    private final Logger logger = LoggerFactory.getLogger(NoteService.class);

    private final NoteProxy noteProxy;

    public NoteService(NoteProxy noteProxy) {
        this.noteProxy = noteProxy;
    }

    /**
     * Retrieves notes for a patient, from the notes-service module
     *
     * @param patientId
     * @return the list of Notes if successful, an empty List if patient has no notes, or null if an error is encountered.
     */
    public List<Note> getNotesByPatientId(int patientId) {
        try {
            ResponseEntity<List<Note>> response = noteProxy.getNotesByPatientId(patientId);
            List<Note> notes = response.getBody();
            int statusCode = response.getStatusCode().value();
            if (statusCode == 200 && notes != null && !notes.isEmpty()) {
                logger.info("Successfully retrieved notes");
                return notes;
            } else if (statusCode == 204) {
                logger.info("No notes found");
                return new ArrayList<>();
            }
            logger.error("Error retrieving notes. Error: {}", statusCode);
            return null;
        } catch (FeignException ex) {
            logger.error("Error retrieving notes: {}", ex.getMessage());
            return null;
        }
    }

    /**
     * SAves a new Note for a given patient.
     *
     * @param note to be saved
     * @return true if successful, or false if an error is encountered.
     */
    public boolean addNote(Note note) {
        try {
            ResponseEntity<Note> response = noteProxy.createNote(note);
            int statusCode = response.getStatusCode().value();
            if (statusCode == 201) {
                logger.info("Successfully added note");
                return true;
            }
            logger.error("Error adding note. Error: {}", statusCode);
            return false;
        } catch (FeignException ex) {
            logger.error("Error adding note: {}", ex.getMessage());
            return false;
        }
    }
}
