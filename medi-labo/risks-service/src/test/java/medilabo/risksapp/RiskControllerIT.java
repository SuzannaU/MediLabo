package medilabo.risksapp;

import medilabo.risksapp.config.SecurityConfig;
import medilabo.risksapp.model.Note;
import medilabo.risksapp.model.Patient;
import medilabo.risksapp.model.RiskLevel;
import medilabo.risksapp.proxy.NoteProxy;
import medilabo.risksapp.proxy.PatientProxy;
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
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class RiskControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientProxy patientProxy;
    @MockitoBean
    private NoteProxy noteProxy;

    private Patient patient;
    private List<Note> notes;

    @BeforeEach
    public void BeforeEach() {
        patient = new Patient();

        notes = new ArrayList<>();
        Note note1 = new Note("rechute, fumer");    // fumer is not a trigger
        Note note2 = new Note("vertige, ANORMALES");
        notes.add(note1);
        notes.add(note2);
    }

    @Test
    @WithMockUser
    public void getRiskLevelByPatientId_shouldReturnRiskAndOk() throws Exception {

        patient.setBirthdate(LocalDate.now().minusYears(52));
        patient.setGender("M");

        when(patientProxy.getPatient(anyInt()))
                .thenReturn(new ResponseEntity<>(patient, HttpStatus.OK));
        when(noteProxy.getNotesByPatientId(anyInt()))
                .thenReturn(new ResponseEntity<>(notes, HttpStatus.OK));

        MvcResult result = mockMvc
                .perform(get("/risks/1"))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertEquals(RiskLevel.BORDERLINE.toString(), resultContent);
        verify(patientProxy).getPatient(anyInt());
        verify(noteProxy).getNotesByPatientId(anyInt());
    }

    @Test
    @WithMockUser
    public void getRiskLevelByPatientId_withNotesException_shouldReturnNAAndOk() throws Exception {

        patient.setBirthdate(LocalDate.now().minusYears(52));
        patient.setGender("M");

        when(patientProxy.getPatient(anyInt()))
                .thenReturn(new ResponseEntity<>(patient, HttpStatus.OK));
        when(noteProxy.getNotesByPatientId(anyInt()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        MvcResult result = mockMvc
                .perform(get("/risks/1"))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertEquals(RiskLevel.NOT_APPLICABLE.toString(), resultContent);
        verify(patientProxy).getPatient(anyInt());
        verify(noteProxy).getNotesByPatientId(anyInt());
    }

    @Test
    @WithMockUser
    public void getRiskLevelByPatientId_withPatientException_shouldReturnNotFound() throws Exception {

        patient.setBirthdate(LocalDate.now().minusYears(52));
        patient.setGender("M");

        when(patientProxy.getPatient(anyInt()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        MvcResult result = mockMvc
                .perform(get("/risks/1"))
                .andExpect(status().isNotFound())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertTrue(resultContent.isEmpty());
        verify(patientProxy).getPatient(anyInt());
    }

    @Test
    @WithMockUser
    public void getRiskLevelByPatientId_withException_shouldReturnServerError() throws Exception {

        patient.setBirthdate(LocalDate.now().minusYears(52));
        patient.setGender("M");

        when(patientProxy.getPatient(anyInt()))
                .thenThrow(new RuntimeException());

        MvcResult result = mockMvc
                .perform(get("/risks/1"))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertTrue(resultContent.isEmpty());
        verify(patientProxy).getPatient(anyInt());
    }
}
