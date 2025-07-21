package medilabo.risksapp.service;

import feign.FeignException;
import medilabo.risksapp.exceptions.NotesNotFoundException;
import medilabo.risksapp.exceptions.PatientNotFoundException;
import medilabo.risksapp.model.Note;
import medilabo.risksapp.model.Patient;
import medilabo.risksapp.model.RiskLevel;
import medilabo.risksapp.model.Triggers;
import medilabo.risksapp.proxy.NoteProxy;
import medilabo.risksapp.proxy.PatientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class RiskService {
    private final Logger logger = LoggerFactory.getLogger(RiskService.class);

    private final NoteProxy noteProxy;
    private final PatientProxy patientProxy;

    public RiskService(NoteProxy noteProxy, PatientProxy patientProxy) {
        this.noteProxy = noteProxy;
        this.patientProxy = patientProxy;
    }

    public RiskLevel calculateRisk(int patientId) {

        Patient patient = getPatientById(patientId);

        Period period = Period.between(patient.getBirthdate(), LocalDate.now());
        int age = period.getYears();
        String gender = patient.getGender();

        int triggers = countMatchingTriggers(patientId);

        if (triggers == 0) {
            return RiskLevel.NONE;
        } else if (age > 30) {
            // trigger = 1 ??
            if (triggers >= 2 && triggers <= 5) {
                return RiskLevel.BORDERLINE;
            } else if (triggers == 6 || triggers == 7) {
                return RiskLevel.IN_DANGER;
            } else if (triggers >= 8) {
                return RiskLevel.EARLY_ONSET;
            }
        } else {
            if (gender.equals("M")) {
                // trigger = 1 ou 2 ??
                if (triggers >= 3 && triggers < 5) {
                    return RiskLevel.IN_DANGER;
                } else if (triggers >= 5) {
                    return RiskLevel.EARLY_ONSET;
                }
            } else if (gender.equals("F")) {
                // trigger = 1 ou 2 ou 3 ??
                if (triggers >= 4 && triggers < 7) {
                    return RiskLevel.IN_DANGER;
                } else if (triggers >= 7) {
                    return RiskLevel.EARLY_ONSET;
                }
            }
        }
        return RiskLevel.NONE;
    }

    private Patient getPatientById(int patientId) {
        try {
            ResponseEntity<Patient> response = patientProxy.getPatient(patientId);
            Patient patient = response.getBody();
            int statusCode = response.getStatusCode().value();
            if (statusCode == 200 && patient != null) {
                logger.info("Patient retrieved successfully");
                return patient;
            }
            throw new PatientNotFoundException("Error retrieving patient with ID " + patientId + ". Status code: " + statusCode);
        } catch (FeignException e) {
            throw new PatientNotFoundException("Error retrieving patient with ID " + patientId, e);
        }
    }

    private int countMatchingTriggers(int patientId) {

        int numTriggers = 0;
        List<Note> notes = getNotesByPatientId(patientId);

        //Concatenate all notes to search for each trigger only once
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
            throw new NotesNotFoundException("Error retrieving notes with patient ID " + patientId + ". Status code: " + statusCode);
        } catch (FeignException e) {
            throw new NotesNotFoundException("Error retrieving notes with patient ID " + patientId, e);
        }
    }
}

