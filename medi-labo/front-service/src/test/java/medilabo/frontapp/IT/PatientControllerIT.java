package medilabo.frontapp.IT;

import medilabo.frontapp.config.SecurityConfig;
import medilabo.frontapp.model.Note;
import medilabo.frontapp.model.Patient;
import medilabo.frontapp.proxy.NoteProxy;
import medilabo.frontapp.proxy.PatientProxy;
import medilabo.frontapp.proxy.RiskProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class PatientControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientProxy patientProxy;
    @MockitoBean
    private NoteProxy noteProxy;
    @MockitoBean
    private RiskProxy riskProxy;

    private Patient patient;

    @BeforeEach
    public void beforeEach() {
        patient = new Patient("firstname", "lastname", LocalDate.now(), "M");

    }

    @Test
    @WithMockUser
    public void getPatients_shouldReturnPatients() throws Exception {

        when(patientProxy.getAllPatients())
                .thenReturn(new ResponseEntity<>(List.of(new Patient()), HttpStatus.OK));

        when(riskProxy.getRiskLevelByPatientId(anyInt()))
                .thenReturn(new ResponseEntity<>("riskLevel", HttpStatus.OK));

        mockMvc.perform(get("/patients"))
                .andExpect(view().name("patients"))
                .andExpect(model().attributeExists("patients"));

        verify(patientProxy).getAllPatients();
    }

    @Test
    @WithMockUser
    public void getPatient_shouldReturnPatientDetails() throws Exception {
        when(patientProxy.getPatient(anyInt()))
                .thenReturn(new ResponseEntity<>(patient, HttpStatus.OK));
        when(noteProxy.getNotesByPatientId(anyInt()))
                .thenReturn(new ResponseEntity<>(List.of(new Note()), HttpStatus.OK));
        when(riskProxy.getRiskLevelByPatientId(anyInt()))
                .thenReturn(new ResponseEntity<>("riskLevel", HttpStatus.OK));

        mockMvc.perform(get("/patients/1"))
                .andExpect(view().name("patient-details"))
                .andExpect(model().attributeExists("patient"));

        assertNotNull(patient.getNotes());
        assertEquals("riskLevel", patient.getRiskLevel());
        verify(patientProxy).getPatient(anyInt());
        verify(noteProxy).getNotesByPatientId(anyInt());
        verify(riskProxy).getRiskLevelByPatientId(anyInt());
    }

    @Test
    @WithMockUser
    public void addPatient_shouldSAveAndRedirect() throws Exception {

        when(patientProxy.createPatient(any(Patient.class)))
                .thenReturn(new ResponseEntity<>(patient, HttpStatus.CREATED));

        mockMvc.perform(post("/patients/add")
                        .flashAttr("patient", patient)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));

        verify(patientProxy).createPatient(any(Patient.class));
    }

    @Test
    @WithMockUser
    public void getEditPatientForm_shouldReturnEditForm() throws Exception {

        when(patientProxy.getPatient(anyInt()))
                .thenReturn(new ResponseEntity<>(patient, HttpStatus.OK));

        mockMvc.perform(get("/patients/edit/1"))
                .andExpect(view().name("update-patient"))
                .andExpect(model().attributeExists("patient"))
                .andExpect(content().string(containsString("firstname")));

        verify(patientProxy).getPatient(anyInt());
    }

    @Test
    @WithMockUser
    public void updatePatient_shouldUpdateAndRedirect() throws Exception {

        int id = 1;
        when(patientProxy.updatePatient(anyInt(), any(Patient.class)))
                .thenReturn(new ResponseEntity<>(patient, HttpStatus.OK));

        mockMvc.perform(post("/patients/edit/" + id)
                        .flashAttr("patient", patient)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients/" + id));

        verify(patientProxy).updatePatient(anyInt(), any(Patient.class));
    }

    @Test
    @WithMockUser
    public void deletePatient_shouldDeleteAndRedirect() throws Exception {

        when(patientProxy.deletePatient(anyInt()))
                .thenReturn(new ResponseEntity<>(patient, HttpStatus.OK));

        mockMvc.perform(post("/patients/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));

        verify(patientProxy).deletePatient(anyInt());
    }
}
