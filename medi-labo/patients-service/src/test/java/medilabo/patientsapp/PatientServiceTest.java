package medilabo.patientsapp;

import medilabo.patientsapp.exceptions.NonExistingPatientException;
import medilabo.patientsapp.model.Patient;
import medilabo.patientsapp.repository.PatientRepo;
import medilabo.patientsapp.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class PatientServiceTest {

    @Autowired
    private PatientService patientService;

    @MockitoBean
    private PatientRepo patientRepo;

    @Test
    public void getAllPatients_shouldCallRepoAndReturnPatients() {
        when(patientRepo.findAll()).thenReturn(List.of(new Patient()));

        List<Patient> patients = patientService.getAllPatients();

        assertEquals(1, patients.size());
        verify(patientRepo).findAll();
    }

    @Test
    public void getPatientById_shouldCallRepoAndReturnPatient() {
        when(patientRepo.findById(anyInt())).thenReturn(Optional.of(new Patient()));

        Patient patient = patientService.getPatientById(1);

        assertNotNull(patient);
        verify(patientRepo).findById(anyInt());
    }

    @Test
    public void getPatientById_withNoPatient_shouldThrow() {
        when(patientRepo.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NonExistingPatientException.class, () -> patientService.getPatientById(1));

        verify(patientRepo).findById(anyInt());
    }

    @Test
    public void addPatient_shouldCallRepoAndReturnPatient() {
        Patient patient = new Patient("firstname", "lastname", "birthdate", "gender");
        when(patientRepo.save(any(Patient.class))).thenReturn(patient);

        Patient savedPatient = patientService.addPatient(patient);

        assertEquals(patient.getId(), savedPatient.getId());
        verify(patientRepo).save(any(Patient.class));
    }

    @Test
    public void updatePatient_shouldCallRepoAndReturnPatient() {
        Patient patient = new Patient("firstname", "lastname", "birthdate", "gender");
        when(patientRepo.save(any(Patient.class))).thenReturn(patient);

        Patient updatedPatient = patientService.updatePatient(patient);

        assertEquals(patient.getId(), updatedPatient.getId());
        verify(patientRepo).save(any(Patient.class));
    }

    @Test
    public void deletePatient_shouldCallRepo() {
        Patient patient = new Patient("firstname", "lastname", "birthdate", "gender");
        doNothing().when(patientRepo).delete(any(Patient.class));

        patientService.deletePatient(patient);

        verify(patientRepo).delete(any(Patient.class));
    }
}
