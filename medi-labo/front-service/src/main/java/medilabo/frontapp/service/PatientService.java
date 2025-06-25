package medilabo.frontapp.service;

import medilabo.frontapp.controller.PatientController;
import medilabo.frontapp.model.Patient;
import medilabo.frontapp.model.PatientDTO;
import medilabo.frontapp.model.PatientMapper;
import medilabo.frontapp.proxy.PatientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        return patientProxy.getAllPatients();
    }

    public Patient getPatient(int id) {
        return patientProxy.getPatient(id);
    }

    public Patient createPatient(PatientDTO patient) {
        return patientProxy.createPatient(patient);
    }

    public Patient updatePatient(int id, PatientDTO patientDTO) {
        Patient existingPatient = getPatient(id);
        PatientMapper.mapPatientDTOToPatient(patientDTO, existingPatient);
        logger.info("Updating patient: {}", existingPatient);
        return patientProxy.updatePatient(id, existingPatient);
    }

    public void deletePatient(int id) {
        patientProxy.deletePatient(id);
    }
}
