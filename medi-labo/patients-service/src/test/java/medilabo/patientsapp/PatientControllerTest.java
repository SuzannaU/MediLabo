package medilabo.patientsapp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import medilabo.patientsapp.config.SecurityConfig;
import medilabo.patientsapp.controller.PatientController;
import medilabo.patientsapp.exceptions.NonExistingPatientException;
import medilabo.patientsapp.model.Patient;
import medilabo.patientsapp.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PatientController.class)
@ActiveProfiles("test")
@Import(SecurityConfig.class)
public class PatientControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PatientService patientService;

    private Patient patient;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void beforeEach() {
        patient = new Patient("firstname", "lastname", LocalDate.now(), "g");

        //Allow Jackson to parse the LocalDate
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @WithMockUser
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
    @WithMockUser
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
    @WithMockUser
    public void getPatientById_shouldReturnPatientAndOk() throws Exception {

        when(patientService.getPatientById(anyInt())).thenReturn(new Patient());

        MvcResult result = mockMvc
                .perform(get("/patients/1"))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        Patient resultPatient = new ObjectMapper().readValue(resultContent, new TypeReference<Patient>() {
        });

        assertNotNull(resultPatient);
        verify(patientService).getPatientById(anyInt());
    }

    @Test
    @WithMockUser
    public void getPatientById_withException_shouldReturnNotFound() throws Exception {

        when(patientService.getPatientById(anyInt())).thenThrow(new NonExistingPatientException());

        MvcResult result = mockMvc
                .perform(get("/patients/1"))
                .andExpect(status().isNotFound())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertTrue(resultContent.isEmpty());
        verify(patientService).getPatientById(anyInt());
    }

    @Test
    @WithMockUser
    public void registerPatient_shouldReturnPatientAndCreated() throws Exception {

        when(patientService.addPatient(any(Patient.class))).thenReturn(patient);

        MvcResult result = mockMvc
                .perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patient)))
                .andExpect(status().isCreated())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        Patient resultPatient = objectMapper.readValue(resultContent, new TypeReference<Patient>() {
        });

        assertEquals(patient.getId(), resultPatient.getId());
        verify(patientService).addPatient(any(Patient.class));
    }

    @ParameterizedTest
    @MethodSource("invalidPatientProvider")
    @WithMockUser
    public void registerPatient_withValidationException_shouldReturnBadRequest(Patient invalidPatient) throws Exception {

        MvcResult result = mockMvc
                .perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPatient)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertEquals("Validation error", resultContent);
    }

    @Test
    @WithMockUser
    public void registerPatient_withException_shouldReturnInternalServerError() throws Exception {

        when(patientService.addPatient(any(Patient.class))).thenThrow(new RuntimeException());

        MvcResult result = mockMvc
                .perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patient)))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertTrue(resultContent.isEmpty());
        verify(patientService).addPatient(any(Patient.class));
    }

    @Test
    @WithMockUser
    public void updatePatient_shouldReturnPatientAndOK() throws Exception {

        when(patientService.updatePatient(any(Patient.class))).thenReturn(patient);

        MvcResult result = mockMvc
                .perform(put("/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patient)))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        Patient resultPatient = objectMapper.readValue(resultContent, new TypeReference<Patient>() {
        });

        assertEquals(patient.getId(), resultPatient.getId());
        verify(patientService).updatePatient(any(Patient.class));
    }

    @ParameterizedTest
    @MethodSource("invalidPatientProvider")
    @WithMockUser
    public void updatePatient_withValidationException_shouldBadRequest(Patient invalidPatient) throws Exception {

        MvcResult result = mockMvc
                .perform(put("/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPatient)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertEquals("Validation error", resultContent);
    }

    @Test
    @WithMockUser
    public void updatePatient_withException_shouldInternalServerError() throws Exception {

        when(patientService.updatePatient(any(Patient.class))).thenThrow(new RuntimeException());

        MvcResult result = mockMvc
                .perform(put("/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patient)))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertTrue(resultContent.isEmpty());
        verify(patientService).updatePatient(any(Patient.class));
    }

    @Test
    @WithMockUser
    public void deletePatient_shouldReturnOk() throws Exception {
        when(patientService.getPatientById(anyInt())).thenReturn(new Patient());
        doNothing().when(patientService).deletePatient(any(Patient.class));

        mockMvc.perform(delete("/patients/1"))
                .andExpect((status().isOk()));

        verify(patientService).deletePatient(any(Patient.class));
    }

    @Test
    @WithMockUser
    public void deletePatient_withException_shouldReturnNotFound() throws Exception {
        when(patientService.getPatientById(anyInt())).thenReturn(new Patient());
        doThrow(new NonExistingPatientException()).when(patientService).deletePatient(any(Patient.class));

        mockMvc.perform(delete("/patients/1"))
                .andExpect((status().isNotFound()));

        verify(patientService).deletePatient(any(Patient.class));
    }

    private static Stream<Arguments> invalidPatientProvider() {
        return Stream.of(
                Arguments.of(new Patient(null, "lastname", LocalDate.now(), "g")),
                Arguments.of(new Patient("firstname", null, LocalDate.now(), "g")),
                Arguments.of(new Patient("firstname", "lastname", null, "g")),
                Arguments.of(new Patient("firstname", "lastname", LocalDate.now(), null)),
                Arguments.of(new Patient("", "lastname", LocalDate.now(), "g")),
                Arguments.of(new Patient("firstname", "", LocalDate.now(), "g")),
                Arguments.of(new Patient("firstname", "lastname", LocalDate.now(), ""))
        );
    }
}
