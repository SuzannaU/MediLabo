package medilabo.frontapp.service;

import feign.FeignException;
import medilabo.frontapp.model.Patient;
import medilabo.frontapp.proxy.PatientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final PatientProxy patientProxy;

    public PatientService(PatientProxy patientProxy) {
        this.patientProxy = patientProxy;
    }

    public List<Patient> getAllPatients() {
        try {
            ResponseEntity<List<Patient>> response = patientProxy.getAllPatients();
            int statusCode = response.getStatusCode().value();
            if (statusCode == 200) {
                logger.info("Successfully retrieved patient list");
                return response.getBody();
            } else if (statusCode == 204) {
                logger.info("No patients found");
                return null;
            } else if (statusCode == 401) {
                logger.info("Unauthorized");
                return null;
            }
            logger.error("Problem retrieving patients. Error: {} ", statusCode);
            return null;
        } catch (FeignException ex) {
            logger.error("Problem retrieving patients.", ex);
            return null;
        }
    }

    public Patient getPatient(int id) {
        try {
            ResponseEntity<Patient> response = patientProxy.getPatient(id);
            int statusCode = response.getStatusCode().value();
            if (statusCode == 200) {
                logger.info("Successfully retrieved patient with id {}", id);
                return response.getBody();
            } else if (statusCode==404) {
                logger.error("No patient found with id {}", id);
                return null;
            }
            logger.error("Problem retrieving patient. Error: {} ", statusCode);
            return null;
        } catch (FeignException e) {
            logger.error("Problem retrieving patient with id {}. Error: {}", id, e.status());
            return null;
        }
    }

    public boolean createPatient(Patient patient) {
        try {
            ResponseEntity<Patient> response = patientProxy.createPatient(patient);
            int statusCode = response.getStatusCode().value();
            if (statusCode == 201) {
                logger.info("Patient successfully created ");
                return true;
            } else if (statusCode == 400){
                logger.error("Validation Error: creating patient unsuccessful");
                return false;
            }
            return false;
        } catch (FeignException e) {
            logger.error("Error: creating patient unsuccessful", e);
            return false;
        }
    }

    public boolean updatePatient(int id, Patient patient) {
        try {
            ResponseEntity<Patient> response = patientProxy.updatePatient(id, patient);
            int statusCode = response.getStatusCode().value();
            if (statusCode == 200) {
                logger.info("Patient with id {} updated successfully", id);
                return true;
            } else if (statusCode == 400){
                logger.error("Validation Error: updating patient with id {} unsuccessful", id);
                return false;
            }
            return false;
        } catch (FeignException e) {
            logger.error("Error: updating patient with id {} unsuccessful", id);
            return false;
        }
    }

    public boolean deletePatient(int id) {
        try {
            ResponseEntity<Patient> response = patientProxy.deletePatient(id);
            int statusCode = response.getStatusCode().value();
            if (statusCode == 200) {
                logger.info("Patient with id {} deleted successfully", id);
                return true;
            } else if (statusCode==404) {
                logger.error("No patient to delete with id {}", id);
                return false;
            }
            logger.error("Problem retrieving patient. Error: {} ", statusCode);
            return false;
        } catch (FeignException e) {
            logger.error("Problem deleting patient with id {}. Error: {}", id, e.status());
            return false;
        }
    }
}
