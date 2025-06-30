package medilabo.frontapp;

import feign.FeignException;
import medilabo.frontapp.model.Patient;
import medilabo.frontapp.model.PatientDTO;
import medilabo.frontapp.proxy.PatientProxy;
import medilabo.frontapp.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class PatientServiceTest {

    @MockitoBean
    private PatientProxy patientProxy;

    @Autowired
    private PatientService patientService;

    @Test
    public void getAllPatients_shouldReturnPatients() {
        when(patientProxy.getAllPatients())
                .thenReturn(new ResponseEntity<>(List.of(new Patient()), HttpStatus.OK));

        List<Patient> patients = patientService.getAllPatients();

         assertEquals(1, patients.size());
         verify(patientProxy).getAllPatients();
    }

    @Test
    public void getAllPatients_with204Code_shouldReturnNull() {
        when(patientProxy.getAllPatients())
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        List<Patient> patients = patientService.getAllPatients();

        assertNull(patients);
        verify(patientProxy).getAllPatients();
    }

    @Test
    public void getAllPatients_withException_shouldReturnNull() {
        when(patientProxy.getAllPatients())
                .thenThrow(new TestFeignException());

        List<Patient> patients = patientService.getAllPatients();

        assertNull(patients);
        verify(patientProxy).getAllPatients();
    }

    @Test
    public void getPatient_shouldReturnPatient() {
        when(patientProxy.getPatient(anyInt()))
                .thenReturn(new ResponseEntity<>(new Patient(), HttpStatus.OK));

        Patient patient = patientService.getPatient(1);

        assertNotNull(patient);
        verify(patientProxy).getPatient(anyInt());
    }

    @Test
    public void getPatient_withNotFoundCode_shouldReturnNull() {
        when(patientProxy.getPatient(anyInt()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        Patient patient = patientService.getPatient(1);

        assertNull(patient);
        verify(patientProxy).getPatient(anyInt());
    }

    @Test
    public void getPatient_withException_shouldReturnNull() {
        when(patientProxy.getPatient(anyInt()))
                .thenThrow(new TestFeignException());

        Patient patient = patientService.getPatient(1);

        assertNull(patient);
        verify(patientProxy).getPatient(anyInt());
    }

    @Test
    public void createPatient_shouldReturnTrue(){

        when(patientProxy.createPatient(any(PatientDTO.class)))
                .thenReturn(new ResponseEntity<>(new Patient(), HttpStatus.CREATED));

        assertTrue(patientService.createPatient(new PatientDTO()));
        verify(patientProxy).createPatient(any(PatientDTO.class));
    }

    @Test
    public void createPatient_withBadRequest_shouldReturnFalse(){

        when(patientProxy.createPatient(any(PatientDTO.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        assertFalse(patientService.createPatient(new PatientDTO()));
        verify(patientProxy).createPatient(any(PatientDTO.class));
    }

    @Test
    public void createPatient_withException_shouldReturnFalse(){

        when(patientProxy.createPatient(any(PatientDTO.class)))
                .thenThrow(new TestFeignException());

        assertFalse(patientService.createPatient(new PatientDTO()));
        verify(patientProxy).createPatient(any(PatientDTO.class));
    }

    @Test
    public void updatePatient_shouldReturnTrue(){

        when(patientProxy.updatePatient(anyInt(), any(Patient.class)))
                .thenReturn(new ResponseEntity<>(new Patient(), HttpStatus.OK));

        assertTrue(patientService.updatePatient(1, new Patient()));
        verify(patientProxy).updatePatient(anyInt(), any(Patient.class));
    }

    @Test
    public void updatePatient_withBadRequest_shouldReturnFalse(){

        when(patientProxy.updatePatient(anyInt(), any(Patient.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        assertFalse(patientService.updatePatient(1, new Patient()));
        verify(patientProxy).updatePatient(anyInt(), any(Patient.class));
    }

    @Test
    public void updatePatient_withException_shouldReturnFalse(){

        when(patientProxy.updatePatient(anyInt(), any(Patient.class)))
                .thenThrow(new TestFeignException());

        assertFalse(patientService.updatePatient(1, new Patient()));
        verify(patientProxy).updatePatient(anyInt(), any(Patient.class));
    }

    @Test
    public void deletePatient_shouldReturnTrue(){

        when(patientProxy.deletePatient(anyInt()))
                .thenReturn(new ResponseEntity<>(new Patient(), HttpStatus.OK));

        assertTrue(patientService.deletePatient(1));
        verify(patientProxy).deletePatient(anyInt());
    }

    @Test
    public void deletePatient_withNotFound_shouldReturnFalse(){

        when(patientProxy.deletePatient(anyInt()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        assertFalse(patientService.deletePatient(1));
        verify(patientProxy).deletePatient(anyInt());
    }

    @Test
    public void deletePatient_withException_shouldReturnFalse(){

        when(patientProxy.deletePatient(anyInt()))
                .thenThrow(new TestFeignException());

        assertFalse(patientService.deletePatient(1));
        verify(patientProxy).deletePatient(anyInt());
    }

    class TestFeignException extends FeignException {
        public TestFeignException() {
            super(400, "Bad Request");
        }
    }

}
