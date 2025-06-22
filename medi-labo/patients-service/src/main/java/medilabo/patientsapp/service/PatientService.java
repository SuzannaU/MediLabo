package medilabo.patientsapp.service;

import medilabo.patientsapp.exceptions.NonExistingPatientException;
import medilabo.patientsapp.model.Patient;
import medilabo.patientsapp.repository.PatientRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepo patientRepo;

    public PatientService(PatientRepo patientRepo) {
        this.patientRepo = patientRepo;
    }

    public List<Patient> getAllPatients() {
        return patientRepo.findAll();
    }

    public Patient getPatientById(int id){
        return patientRepo.findById(id).orElseThrow(()->new NonExistingPatientException("Patient not found for id: " + id));
    }

    public Patient addPatient(Patient patient) {
        return patientRepo.save(patient);
    }

    public Patient updatePatient(Patient patient) {
            return patientRepo.save(patient);
    }

    public void deletePatient(Patient patient) {
        patientRepo.delete(patient);
    }
}
