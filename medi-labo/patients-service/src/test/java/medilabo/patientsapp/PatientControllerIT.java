package medilabo.patientsapp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class PatientControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepo patientRepo;

    private Patient validPatient;

    @BeforeEach
    public void setup() {
        validPatient = new Patient("firstname", "lastname", "birthdate", "g");
    }

    private static Stream<Arguments> invalidPatientProvider() {
        return Stream.of(
                Arguments.of(new Patient(null, "lastname", "birthdate", "g")),
                Arguments.of(new Patient("firstname", null, "birthdate", "g")),
                Arguments.of(new Patient("firstname", "lastname", null, "g")),
                Arguments.of(new Patient("firstname", "lastname", "birthdate", null)),
                Arguments.of(new Patient("", "lastname", "birthdate", "g")),
                Arguments.of(new Patient("firstname", "", "birthdate", "g")),
                Arguments.of(new Patient("firstname", "lastname", "", "g")),
                Arguments.of(new Patient("firstname", "lastname", "birthdate", ""))
        );
    }

    @Test
    public void getAllPatients_shouldReturnPatientsAndOk() throws Exception {

        MvcResult result = mockMvc
                .perform(get("/patients"))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        List<Patient> resultPatients = new ObjectMapper()
                .readValue(resultContent, new TypeReference<List<Patient>>() {
                });

        assertEquals(4, resultPatients.size());
    }

    @Test
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
    public void getPatientById_shouldReturnPatientAndOk() throws Exception {

        MvcResult result = mockMvc
                .perform(get("/patients/1"))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        Patient resultPatient = new ObjectMapper()
                .readValue(resultContent, new TypeReference<Patient>() {
                });

        assertNotNull(resultPatient);
        assertEquals(1, resultPatient.getId());
    }

    @Test
    public void getPatientById_withBadId_shouldReturnNotFound() throws Exception {

        MvcResult result = mockMvc
                .perform(get("/patients/123456789"))
                .andExpect(status().isNotFound())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertTrue(resultContent.isEmpty());
    }

    @Test
    public void registerPatient_shouldReturnPatientAndCreated() throws Exception {

        MvcResult result = mockMvc
                .perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validPatient)))
                .andExpect(status().isCreated())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        Patient resultPatient = new ObjectMapper()
                .readValue(resultContent, new TypeReference<Patient>() {
                });

        assertEquals(validPatient.getFirstname(), resultPatient.getFirstname());
    }

    @ParameterizedTest
    @MethodSource("invalidPatientProvider")
    public void registerPatient_withInvalidPatient_shouldReturnBadRequest(Patient invalidPatient) throws Exception {

        MvcResult result = mockMvc
                .perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidPatient)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertTrue(resultContent.isEmpty());
    }

    @Test
    public void updatePatient_shouldReturnPatientAndOK() throws Exception {

        MvcResult result = mockMvc
                .perform(put("/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validPatient)))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        Patient resultPatient = new ObjectMapper()
                .readValue(resultContent, new TypeReference<Patient>() {
                });

        assertEquals(validPatient.getFirstname(), resultPatient.getFirstname());
    }

    @ParameterizedTest
    @MethodSource("invalidPatientProvider")
    public void updatePatient_withInvalidPatient_shouldReturnBadRequest(Patient invalidPatient) throws Exception {

        MvcResult result = mockMvc
                .perform(put("/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidPatient)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertTrue(resultContent.isEmpty());
    }

    @Test
    public void deletePatient_shouldDeletePatientAndReturnOk() throws Exception {

        int id = 1;
        mockMvc.perform(delete("/patients/" + id))
                .andExpect((status().isOk()));

        assertTrue(patientRepo.findById(id).isEmpty());
    }

    @Test
    public void deletePatient_withInvalidId_shouldReturnNotFound() throws Exception {

        mockMvc.perform(delete("/patients/123456789"))
                .andExpect(status().isNotFound());
    }
}
