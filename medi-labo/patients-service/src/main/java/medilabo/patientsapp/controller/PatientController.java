package medilabo.patientsapp.controller;

import jakarta.validation.Valid;
import medilabo.patientsapp.exceptions.NonExistingPatientException;
import medilabo.patientsapp.model.Patient;
import medilabo.patientsapp.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/patients")
@Validated
public class PatientController {
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        if (patients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable("id") int id) {
        try {
            return new ResponseEntity<>(patientService.getPatientById(id), HttpStatus.OK);
        } catch (NonExistingPatientException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<Patient> registerPatient(@RequestBody @Valid Patient patient) {
        try {
            return new ResponseEntity<>(patientService.addPatient(patient), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Patient was not created : {}", patient.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@RequestBody @Valid Patient patient) {
        try {
            return new ResponseEntity<>(patientService.updatePatient(patient), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Patient was not updated : {}", patient.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Patient> deletePatient(@PathVariable("id") int id) {
        try {
            patientService.deletePatient(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NonExistingPatientException e) {
            logger.error("Deletion failed for Patient ID: {}. ID not found.", id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
