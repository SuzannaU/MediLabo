package medilabo.risksapp.service;

import medilabo.risksapp.model.Note;
import medilabo.risksapp.model.Patient;
import medilabo.risksapp.model.RiskLevel;
import medilabo.risksapp.model.Triggers;
import medilabo.risksapp.proxy.NoteProxy;
import medilabo.risksapp.proxy.PatientProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class RiskService {

    private final NoteProxy noteProxy;
    private final PatientProxy patientProxy;

    public RiskService(NoteProxy noteProxy, PatientProxy patientProxy) {
        this.noteProxy = noteProxy;
        this.patientProxy = patientProxy;
    }

    public RiskLevel calculateRisk(int patientId) {
        // retrieve patient
        ResponseEntity<Patient> response = patientProxy.getPatient(patientId);
        Patient patient = response.getBody();
        if (patient != null && response.getStatusCode().value() == 200) {

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
        }
        return RiskLevel.NONE;
    }

    public int countMatchingTriggers(int patientId) {

        int numTriggers = 0;

        ResponseEntity<List<Note>> response = noteProxy.getNotesByPatientId(patientId);
        List<Note> notes = response.getBody();
        if (notes != null && response.getStatusCode().value() == 200) {
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
        }
        return numTriggers;
    }
}
