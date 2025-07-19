package medilabo.frontapp.service;

import feign.FeignException;
import medilabo.frontapp.model.Note;
import medilabo.frontapp.proxy.NoteProxy;
import medilabo.frontapp.proxy.PatientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {
    private final Logger logger = LoggerFactory.getLogger(NoteService.class);

    private final NoteProxy noteProxy;
    public NoteService(NoteProxy noteProxy) {
        this.noteProxy = noteProxy;
    }

    public List<Note> getNotesByPatientId(int patientId) {
        try{
            ResponseEntity<List<Note>> response = noteProxy.getNotesByPatientId(patientId);
            int statusCode = response.getStatusCode().value();
            if(statusCode == 200){
                logger.info("Successfully retrieved notes");
                return response.getBody();
            } else if(statusCode == 204) {
                logger.info("No notes found");
                return null;
            } else if(statusCode == 401){
                logger.info("Unauthorized");
                return null;
            }
            logger.error("Error retrieving notes. Error: {}", statusCode);
            return null;
        } catch (FeignException ex) {
            logger.error("Error retrieving notes: {}", ex.getMessage());
            return null;
        }
    }

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
