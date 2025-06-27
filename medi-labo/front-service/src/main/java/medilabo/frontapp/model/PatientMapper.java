package medilabo.frontapp.model;

import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public static void mapPatientDTOToPatient(PatientDTO patientDTO, Patient patient) {
        patient.setFirstname(patientDTO.getFirstname());
        patient.setLastname(patientDTO.getLastname());
        patient.setBirthdate(patientDTO.getBirthdate());
        patient.setGender(patientDTO.getGender());
        patient.setAddress(patientDTO.getAddress());
        patient.setPhoneNumber(patientDTO.getPhoneNumber());
    }

    public static void mapPatientToPatientDTO(PatientDTO patientDTO, Patient patient) {
        patientDTO.setFirstname(patient.getFirstname());
        patientDTO.setLastname(patient.getLastname());
        patientDTO.setBirthdate(patient.getBirthdate());
        patientDTO.setGender(patient.getGender());
        patientDTO.setAddress(patient.getAddress());
        patientDTO.setPhoneNumber(patient.getPhoneNumber());
    }
}
