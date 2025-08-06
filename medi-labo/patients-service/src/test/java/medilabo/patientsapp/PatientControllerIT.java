package medilabo.patientsapp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import medilabo.patientsapp.config.SecurityConfig;
import medilabo.patientsapp.model.Patient;
import medilabo.patientsapp.repository.PatientRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
@Transactional
public class PatientControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepo patientRepo;

    private Patient validPatient;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        validPatient = new Patient("firstname", "lastname", LocalDate.now(), "g");

        //Allow Jackson to parse the LocalDate
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @WithMockUser
    public void getAllPatients_shouldReturnPatientsAndOk() throws Exception {

        MvcResult result = mockMvc
                .perform(get("/patients"))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        List<Patient> resultPatients = objectMapper
                .readValue(resultContent, new TypeReference<List<Patient>>() {
                });

        assertEquals(4, resultPatients.size());
    }

    @Test
    @WithMockUser
    public void getAllPatients_withNoPatients_shouldReturnNoContent() throws Exception {

        patientRepo.deleteAll();

        MvcResult result = mockMvc
                .perform(get("/patients"))
                .andExpect(status().isNoContent())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertTrue(resultContent.isEmpty());
    }

    @Test
    @WithMockUser
    public void getPatientById_shouldReturnPatientAndOk() throws Exception {

        MvcResult result = mockMvc
                .perform(get("/patients/1"))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        Patient resultPatient = objectMapper
                .readValue(resultContent, new TypeReference<Patient>() {
                });

        assertNotNull(resultPatient);
        assertEquals(1, resultPatient.getId());
    }

    @Test
    @WithMockUser
    public void getPatientById_withBadId_shouldReturnNotFound() throws Exception {

        MvcResult result = mockMvc
                .perform(get("/patients/123456789"))
                .andExpect(status().isNotFound())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertTrue(resultContent.isEmpty());
    }

    @Test
    @WithMockUser
    public void registerPatient_shouldReturnPatientAndCreated() throws Exception {

        MvcResult result = mockMvc
                .perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPatient)))
                .andExpect(status().isCreated())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        Patient resultPatient = objectMapper
                .readValue(resultContent, new TypeReference<Patient>() {
                });

        assertEquals(validPatient.getFirstname(), resultPatient.getFirstname());
    }

    @ParameterizedTest
    @MethodSource("invalidPatientProvider")
    @WithMockUser
    public void registerPatient_withInvalidPatient_shouldReturnBadRequest(Patient invalidPatient) throws Exception {

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
    public void updatePatient_shouldReturnPatientAndOK() throws Exception {

        MvcResult result = mockMvc
                .perform(put("/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPatient)))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        Patient resultPatient = objectMapper
                .readValue(resultContent, new TypeReference<Patient>() {
                });

        assertEquals(validPatient.getFirstname(), resultPatient.getFirstname());
    }

    @ParameterizedTest
    @MethodSource("invalidPatientProvider")
    @WithMockUser
    public void updatePatient_withInvalidPatient_shouldReturnBadRequest(Patient invalidPatient) throws Exception {

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
    public void deletePatient_shouldDeletePatientAndReturnOk() throws Exception {

        int id = 1;
        mockMvc.perform(delete("/patients/" + id))
                .andExpect((status().isOk()));

        assertTrue(patientRepo.findById(id).isEmpty());
    }

    @Test
    @WithMockUser
    public void deletePatient_withInvalidId_shouldReturnNotFound() throws Exception {

        mockMvc.perform(delete("/patients/123456789"))
                .andExpect(status().isNotFound());
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
