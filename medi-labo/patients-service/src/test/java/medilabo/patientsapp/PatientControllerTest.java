package medilabo.patientsapp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import medilabo.patientsapp.controller.PatientController;
import medilabo.patientsapp.exceptions.NonExistingPatientException;
import medilabo.patientsapp.model.Patient;
import medilabo.patientsapp.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PatientController.class)
public class PatientControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PatientService patientService;

    @Test
    public void getAllPatients_shouldReturnPatientsAndOk() throws Exception {
        when(patientService.getAllPatients()).thenReturn(List.of(new Patient()));

        MvcResult result = mockMvc
                .perform(get("/patients"))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        List<Patient> resultPatients = new ObjectMapper()
                .readValue(resultContent, new TypeReference<List<Patient>>() {
                });

        assertEquals(1, resultPatients.size());
        verify(patientService).getAllPatients();
    }

    @Test
    public void getAllPatients_withNoPatients_shouldReturnNoContent() throws Exception {
        when(patientService.getAllPatients()).thenReturn(new ArrayList<>());

        MvcResult result = mockMvc
                .perform(get("/patients"))
                .andExpect(status().isNoContent())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertTrue(resultContent.isEmpty());
        verify(patientService).getAllPatients();
    }

    @Test
    public void getPatientById_shouldReturnPatientAndOk() throws Exception {
        when(patientService.getPatientById(anyInt())).thenReturn(new Patient());

        MvcResult result = mockMvc
                .perform(get("/patients/1"))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        Patient resultPatient = new ObjectMapper()
                .readValue(resultContent, new TypeReference<Patient>() {
                });

        assertNotNull(resultPatient);
        verify(patientService).getPatientById(anyInt());
    }

    @Test
    public void getPatientById_withException_shouldReturnBadRequest() throws Exception {
        when(patientService.getPatientById(anyInt())).thenThrow(new NonExistingPatientException());

        MvcResult result = mockMvc
                .perform(get("/patients/1"))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertTrue(resultContent.isEmpty());
        verify(patientService).getPatientById(anyInt());
    }

    @Test
    public void registerPatient_shouldReturnPatientAndCreated() throws Exception {
        Patient patient = new Patient(1, "firstname", "lastname", "birthdate", "gender");
        when(patientService.addPatient(any(Patient.class))).thenReturn(patient);

        MvcResult result = mockMvc
                .perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(patient)))
                .andExpect(status().isCreated())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        Patient resultPatient = new ObjectMapper()
                .readValue(resultContent, new TypeReference<Patient>() {
                });

        assertEquals(patient.getId(), resultPatient.getId());
        verify(patientService).addPatient(any(Patient.class));
    }

    @Test
    public void registerPatient_withException_shouldReturnBadRequest() throws Exception {
        Patient patient = new Patient(1, "firstname", "lastname", "birthdate", "gender");
        // TODO find real exception instead of this one
        when(patientService.addPatient(any(Patient.class))).thenThrow(new NonExistingPatientException());

        MvcResult result = mockMvc
                .perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(patient)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertTrue(resultContent.isEmpty());
        verify(patientService).addPatient(any(Patient.class));
    }

    @Test
    public void updatePatient_shouldReturnPatientAndOK() throws Exception {
        Patient patient = new Patient(1, "firstname", "lastname", "birthdate", "gender");
        when(patientService.updatePatient(any(Patient.class))).thenReturn(patient);

        MvcResult result = mockMvc
                .perform(put("/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(patient)))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        Patient resultPatient = new ObjectMapper()
                .readValue(resultContent, new TypeReference<Patient>() {
                });

        assertEquals(patient.getId(), resultPatient.getId());
        verify(patientService).updatePatient(any(Patient.class));
    }

    @Test
    public void updatePatient_withException_shouldReturnBadRequest() throws Exception {
        Patient patient = new Patient(1, "firstname", "lastname", "birthdate", "gender");
        // TODO find real exception instead of this one
        when(patientService.updatePatient(any(Patient.class))).thenThrow(new NonExistingPatientException());

        MvcResult result = mockMvc
                .perform(put("/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(patient)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertTrue(resultContent.isEmpty());
        verify(patientService).updatePatient(any(Patient.class));
    }

    @Test
    public void deletePatient_shouldReturnOk() throws Exception {
        doNothing().when(patientService).deletePatient(anyInt());

        mockMvc.perform(delete("/patients/1"))
                .andExpect((status().isOk()));

        verify(patientService).deletePatient(anyInt());
    }

    @Test
    public void deletePatient_withException_shouldReturnBadRequest() throws Exception {
        // TODO find real exception instead of this one
        doThrow(new NonExistingPatientException()).when(patientService).deletePatient(anyInt());

        mockMvc.perform(delete("/patients/1"))
                .andExpect((status().isBadRequest()));

        verify(patientService).deletePatient(anyInt());
    }
}
