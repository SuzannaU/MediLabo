package medilabo.frontapp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import medilabo.frontapp.config.CustomProperties;
import medilabo.frontapp.controller.PatientController;
import medilabo.frontapp.model.Patient;
import medilabo.frontapp.model.PatientDTO;
import medilabo.frontapp.service.PatientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@Import({CustomProperties.class})
@ActiveProfiles("test")
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomProperties customProperties;

    @MockitoBean
    private PatientService patientService;

    @ParameterizedTest
    @ValueSource(strings = {"", "/", "/patients"})
    public void getPatients_shouldReturnPatients(String arg) throws Exception {

        when(patientService.getAllPatients()).thenReturn(List.of(new Patient()));

        mockMvc.perform(get(arg)).andExpect(view().name("patients"));

        verify(patientService).getAllPatients();
    }

    @Test
    public void getPatient_shouldReturnPatientDetails() throws Exception {

        when(patientService.getPatient(anyInt())).thenReturn(new Patient());

        mockMvc.perform(get("/patients/1")).andExpect(view().name("patient-details"));

        verify(patientService).getPatient(anyInt());
    }

    @Test
    public void getNewPatientForm_shouldReturnForm() throws Exception {

        mockMvc.perform(get("/patients/add")).andExpect(view().name("patient-form"));
    }

    @Test
    public void addPatient_shouldSAveAndRedirect() throws Exception {

        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstname("firstname");
        patientDTO.setLastname("lastname");
        patientDTO.setBirthdate("birthdate");
        patientDTO.setGender("g");
        when(patientService.createPatient(any(PatientDTO.class))).thenReturn(true);

        mockMvc.perform(post("/patients/add").flashAttr("patient", patientDTO)).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl(customProperties.getBaseUrl() + "/patients"));

        verify(patientService).createPatient(patientDTO);
    }

    @Test
    public void getEditPatientForm_shouldReturnEditForm() throws Exception {

        Patient patient = new Patient();
        patient.setFirstname("firstname");
        when(patientService.getPatient(anyInt())).thenReturn(patient);

        mockMvc.perform(get("/patients/edit/1")).andExpect(view().name("update-patient")).andExpect(content().string(containsString("firstname")));

        verify(patientService).getPatient(anyInt());
    }

    @Test
    public void updatePatient_shouldUpdateAndRedirect() throws Exception {

        int id = 1;
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstname("firstname");
        patientDTO.setLastname("lastname");
        patientDTO.setBirthdate("birthdate");
        patientDTO.setGender("g");
        when(patientService.updatePatient(anyInt(), any(Patient.class))).thenReturn(true);

        mockMvc.perform(post("/patients/edit/" + id)
                .flashAttr("patient", patientDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(customProperties.getBaseUrl() + "/patients/" + id));
    }

    @Test
    public void deletePatient_shouldDeleteAndRedirect() throws Exception {

        doNothing().when(patientService).deletePatient(anyInt());

        mockMvc.perform(post("/patients/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(customProperties.getBaseUrl() + "/patients"));

        verify(patientService).deletePatient(anyInt());
    }

}
