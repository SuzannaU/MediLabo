package medilabo.frontapp.service;

import feign.FeignException;
import medilabo.frontapp.model.Patient;
import medilabo.frontapp.proxy.PatientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service used to handle Response entities received from the proxy interface, regarding Patient objects.
 *
 * @see PatientProxy
 */
@Service
public class PatientService {
    private final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final PatientProxy patientProxy;
    private final RiskService riskService;

    public PatientService(PatientProxy patientProxy, RiskService riskService) {
        this.patientProxy = patientProxy;
        this.riskService = riskService;
    }

    /**
     * Retrieves a List of all the patients from the patients-service module.
     *
     * @return the List of all the patients if successful, or null if there are no patients or if an error is encountered.
     */
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
            }
            logger.error("Problem retrieving patients. Error: {} ", statusCode);
            return null;
        } catch (FeignException ex) {
            logger.error("Problem retrieving patients.", ex);
            return null;
        }
    }

    /**
     * Retrieves a patient according to its id.
     *
     * @param id
     * @return the patient, or null if an error occurs.
     */
    public Patient getPatient(int id) {
        try {
            ResponseEntity<Patient> response = patientProxy.getPatient(id);
            int statusCode = response.getStatusCode().value();
            if (statusCode == 200) {
                logger.info("Successfully retrieved patient with id {}", id);
                return response.getBody();
            }
            logger.error("Problem retrieving patient. Error: {} ", statusCode);
            return null;
        } catch (FeignException e) {
            logger.error("Problem retrieving patient with id {}. Error: {}", id, e.status());
            return null;
        }
    }

    /**
     * Saves a new patient.
     *
     * @param patient to be saved
     * @return true if successful, false if any error occurs.
     */
    public boolean createPatient(Patient patient) {
        try {
            ResponseEntity<Patient> response = patientProxy.createPatient(patient);
            int statusCode = response.getStatusCode().value();
            if (statusCode == 201) {
                logger.info("Patient successfully created ");
                return true;
            }
            logger.error("Problem creating patient. Error: {} ", statusCode);
            return false;
        } catch (FeignException.BadRequest e) {
            logger.error("Validation Error: creating patient unsuccessful", e);
            return false;
        } catch (FeignException e) {
            logger.error("Error: creating patient unsuccessful", e);
            return false;
        }
    }

    /**
     * Updates a patient.
     *
     * @param id      the id of the patient to be updated
     * @param patient
     * @return true if update is successful, false if any error occurs.
     */
    public boolean updatePatient(int id, Patient patient) {
        try {
            ResponseEntity<Patient> response = patientProxy.updatePatient(id, patient);
            int statusCode = response.getStatusCode().value();
            if (statusCode == 200) {
                riskService.risksCache.remove(id);
                logger.info("Patient with id {} updated successfully. RiskLevel removed from cache.", id);
                return true;
            }
            logger.error("Problem updating patient. Error: {} ", statusCode);
            return false;
        } catch (FeignException.BadRequest e) {
            logger.error("Validation Error: updating patient unsuccessful", e);
            return false;
        } catch (FeignException e) {
            logger.error("Error: updating patient with id {} unsuccessful", id);
            return false;
        }
    }

    /**
     * Deletes a patient according to its id.
     *
     * @param id
     * @return true if deletion is successful, false if an error occurs.
     */
    public boolean deletePatient(int id) {
        try {
            ResponseEntity<Patient> response = patientProxy.deletePatient(id);
            int statusCode = response.getStatusCode().value();
            if (statusCode == 200) {
                logger.info("Patient with id {} deleted successfully", id);
                return true;
            }
            logger.error("Problem deleting patient. Error: {} ", statusCode);
            return false;
        } catch (FeignException.NotFound e) {
            logger.error("No patient to delete with id {}. Error: {}", id, e.status());
            return false;
        } catch (FeignException e) {
            logger.error("Problem deleting patient with id {}. Error: {}", id, e.status());
            return false;
        }
    }
}
