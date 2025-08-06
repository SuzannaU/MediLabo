package medilabo.patientsapp.controller;

import jakarta.validation.Valid;
import medilabo.patientsapp.exceptions.NonExistingPatientException;
import medilabo.patientsapp.model.Patient;
import medilabo.patientsapp.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller that handles requests related to Patient type.
 * It calls methods form PatientService
 *
 * @see PatientService
 */
@RestController
@RequestMapping("/patients")
@Validated
public class PatientController {
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * Handles MethodArgumentNotValidException for the whole Controller
     *
     * @param e the thrown exception
     * @return ResponseEntity with 404 and error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation error");
    }

    /**
     * Gets the List of all patients.
     *
     * @return a ResponseEntity containing the List with 200 code, or with 204 if there are no Patients
     */
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        logger.info("GetMapping for /patients");
        List<Patient> patients = patientService.getAllPatients();
        if (patients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    /**
     * Gets patient by id.
     *
     * @param id the id
     * @return ResponseEntity with the patient and 200, or ResponseEntity with 404 if no patient is found with this id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable("id") int id) {
        logger.info("GetMapping for /patients/{}", id);
        try {
            return new ResponseEntity<>(patientService.getPatientById(id), HttpStatus.OK);
        } catch (NonExistingPatientException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Registers a new Patient.
     *
     * @param patient the patient
     * @return ResponseEntity with the saved patient and 201 code, or ResponseEntity with 500 if an exception occurs
     */
    @PostMapping
    public ResponseEntity<Patient> registerPatient(@RequestBody @Valid Patient patient) {
        logger.info("PostMapping for /patients");
        try {
            return new ResponseEntity<>(patientService.addPatient(patient), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Patient was not created : {}", patient.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates an existing patient.
     *
     * @param id      the id of the existing patient
     * @param patient the patient with data to update
     * @return ResponseEntity with the updated patient and 200 code, or ResponseEntity with 500 if an Exception occurs
     */
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable("id") int id, @RequestBody @Valid Patient patient) {
        logger.info("PutMapping for /patients/{}", id);
        try {
            return new ResponseEntity<>(patientService.updatePatient(patient), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Patient was not updated : {}", patient.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a patient.
     *
     * @param id the id of the patient to be deleted
     * @return ResponseEntity with 200 code, or ResponseEntity with 404 if no Patient is found with this id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Patient> deletePatient(@PathVariable("id") int id) {
        logger.info("DeleteMapping for /patients/{}", id);
        try {
            Patient patient = patientService.getPatientById(id);
            patientService.deletePatient(patient);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NonExistingPatientException e) {
            logger.error("Deletion failed for Patient ID: {}. ID not found.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
