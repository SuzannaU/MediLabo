package medilabo.patientsapp.service;

import medilabo.patientsapp.exceptions.NonExistingPatientException;
import medilabo.patientsapp.model.Patient;
import medilabo.patientsapp.repository.PatientRepo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service that performs operations on Patient objects. Its methods are called by PatientController, it calls PatientRepo to perform operations on the Database.
 *
 * @see medilabo.patientsapp.controller.PatientController
 * @see PatientRepo
 */
@Service
public class PatientService {

    private final PatientRepo patientRepo;

    public PatientService(PatientRepo patientRepo) {
        this.patientRepo = patientRepo;
    }

    /**
     * Gets all patients.
     *
     * @return a List of all the patients
     */
    public List<Patient> getAllPatients() {
        return patientRepo.findAll();
    }

    /**
     * Get patient by id.
     *
     * @param id the id
     * @return the patient
     * @throws NonExistingPatientException if no patient exists with that id
     */
    public Patient getPatientById(int id){
        return patientRepo.findById(id).orElseThrow(()->new NonExistingPatientException("Patient not found for id: " + id));
    }

    /**
     * Adds a new Patient.
     *
     * @param patient the patient
     * @return the saved patient
     */
    public Patient addPatient(Patient patient) {
        return patientRepo.save(patient);
    }

    /**
     * Updates an existing patient.
     *
     * @param patient the data to be updated
     * @return the updated patient
     */
    public Patient updatePatient(Patient patient) {
            return patientRepo.save(patient);
    }

    /**
     * Deletes patient.
     *
     * @param patient the patient to be deleted
     */
    public void deletePatient(Patient patient) {
        patientRepo.delete(patient);
    }
}
