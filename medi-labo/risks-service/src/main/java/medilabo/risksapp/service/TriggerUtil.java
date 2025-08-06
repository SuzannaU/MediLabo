package medilabo.risksapp.service;

import feign.FeignException;
import medilabo.risksapp.exceptions.NotesNotFoundException;
import medilabo.risksapp.model.Note;
import medilabo.risksapp.model.Triggers;
import medilabo.risksapp.proxy.NoteProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Utility service that calculates triggers on Notes. Its method is used in RiskService class.
 *
 * @see RiskService
 * @see Triggers
 */
@Service
public class TriggerUtil {
    private final Logger logger = LoggerFactory.getLogger(TriggerUtil.class);

    private final NoteProxy noteProxy;

    public TriggerUtil(NoteProxy noteProxy) {
        this.noteProxy = noteProxy;
    }

    /**
     * Uses private method to recover a list of Notes from a patient ID, then counts the number of triggers that are present in the notes. The number of occurrences for each trigger is not relevant.
     * If Exceptions are thrown, they are handled down the line in RiskController.
     *
     * @param patientId
     * @return numTrigger - can be 0
     */
    public int countMatchingTriggers(int patientId) {

        int numTriggers = 0;
        List<Note> notes = getNotesByPatientId(patientId);

        //Concatenate all patient notes to search for each trigger only once
        StringBuilder allNotes = new StringBuilder();
        for (Note note : notes) {
            String content = note.getContent().toLowerCase();
            allNotes.append(content);
        }

        for (String trigger : Triggers.getTriggers()) {
            if (allNotes.toString().contains(trigger.toLowerCase())) {
                numTriggers++;
            }
        }
        return numTriggers;
    }

    private List<Note> getNotesByPatientId(int patientId) {
        try {
            ResponseEntity<List<Note>> response = noteProxy.getNotesByPatientId(patientId);
            List<Note> notes = response.getBody();
            int statusCode = response.getStatusCode().value();
            if (statusCode == 200 && notes != null) {
                logger.info("Notes retrieved successfully");
                return notes;
            }
            logger.error("Problem occurred retrieving notes. StatusCode: {}", statusCode);
            throw new NotesNotFoundException("Error retrieving notes with patient ID " + patientId + ". Status code: " + statusCode);
        } catch (FeignException e) {
            throw new NotesNotFoundException("Error retrieving notes with patient ID " + patientId, e);
        }
    }

}
