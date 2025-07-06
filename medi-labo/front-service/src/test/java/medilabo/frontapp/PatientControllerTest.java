package medilabo.frontapp;

import medilabo.frontapp.config.CustomProperties;
import medilabo.frontapp.controller.PatientController;
import medilabo.frontapp.model.Patient;
import medilabo.frontapp.model.PatientDTO;
import medilabo.frontapp.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

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

    @MockitoBean
    private PatientService patientService;

    private Patient validPatient;
    private PatientDTO validPatientDTO;

    @BeforeEach
    public void beforeEach() {
        validPatientDTO = new PatientDTO("firstname", "lastname", "birthdate", "g");
        validPatient = new Patient("firstname", "lastname", new Date(), "g");

    }

    private static Stream<Arguments> invalidPatientDTOProvider() {
        return Stream.of(
                Arguments.of(new PatientDTO(null, "lastname", "birthdate", "g")),
                Arguments.of(new PatientDTO("firstname", null, "birthdate", "g")),
                Arguments.of(new PatientDTO("firstname", "lastname", null, "g")),
                Arguments.of(new PatientDTO("firstname", "lastname", "birthdate", null)),
                Arguments.of(new PatientDTO("", "lastname", "birthdate", "g")),
                Arguments.of(new PatientDTO("firstname", "", "birthdate", "g")),
                Arguments.of(new PatientDTO("firstname", "lastname", "", "g")),
                Arguments.of(new PatientDTO("firstname", "lastname", "birthdate", "")),
                Arguments.of(new PatientDTO("firstname", "lastname", "birthdate", "gender")));
    }

    private static Stream<Arguments> invalidPatientProvider() {
        return Stream.of(
                Arguments.of(new Patient(null, "lastname", new Date(), "g")),
                Arguments.of(new Patient("firstname", null, new Date(), "g")),
                Arguments.of(new Patient("firstname", "lastname", null, "g")),
                Arguments.of(new Patient("firstname", "lastname", new Date(), null)),
                Arguments.of(new Patient("", "lastname", new Date(), "g")),
                Arguments.of(new Patient("firstname", "", new Date(), "g")),
                Arguments.of(new Patient("firstname", "lastname", new Date(), "")),
                Arguments.of(new Patient("firstname", "lastname", new Date(), "gender")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "/", "/patients"})
    public void getPatients_shouldReturnPatients(String arg) throws Exception {

        when(patientService.getAllPatients()).thenReturn(List.of(new Patient()));

        mockMvc.perform(get(arg))
                .andExpect(view().name("patients"))
                .andExpect(model().attributeExists("patients"));

        verify(patientService).getAllPatients();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "/", "/patients"})
    public void getPatients_withNoPatients_shouldReturnPatientsWithError(String arg) throws Exception {

        when(patientService.getAllPatients()).thenReturn(null);

        mockMvc.perform(get(arg))
                .andExpect(view().name("patients"))
                .andExpect(model().attributeDoesNotExist("patients"))
                .andExpect(model().attributeExists("emptyListError"));

        verify(patientService).getAllPatients();
    }

    @Test
    public void getPatient_shouldReturnPatientDetails() throws Exception {

        when(patientService.getPatient(anyInt())).thenReturn(new Patient());

        mockMvc.perform(get("/patients/1"))
                .andExpect(view().name("patient-details"))
                .andExpect(model().attributeExists("patient"));

        verify(patientService).getPatient(anyInt());
    }

    @Test
    public void getPatient_withNoPatient_shouldReturnError() throws Exception {

        when(patientService.getPatient(anyInt())).thenReturn(null);

        mockMvc.perform(get("/patients/1"))
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("status", "error", "message"));

        verify(patientService).getPatient(anyInt());
    }

    @Test
    public void getNewPatientForm_shouldReturnForm() throws Exception {

        mockMvc.perform(get("/patients/add")).andExpect(view().name("patient-form"));
    }

    @Test
    public void addPatient_shouldSAveAndRedirect() throws Exception {

        when(patientService.createPatient(any(PatientDTO.class))).thenReturn(true);

        mockMvc.perform(post("/patients/add").flashAttr("patient", validPatientDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));

        verify(patientService).createPatient(any(PatientDTO.class));
    }

    @Test
    public void addPatient_withError_shouldReturnFormWithError() throws Exception {

        when(patientService.createPatient(any(PatientDTO.class))).thenReturn(false);

        mockMvc.perform(post("/patients/add").flashAttr("patient", validPatientDTO))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("patient-form"))
                .andExpect(model().attributeExists("newPatientError", "patient"));

        verify(patientService).createPatient(any(PatientDTO.class));
    }

    @ParameterizedTest
    @MethodSource("invalidPatientDTOProvider")
    public void addPatient_withInvalidPatient_shouldReturnFormWithError(PatientDTO invalidPatientDTO) throws Exception {

        mockMvc.perform(post("/patients/add").flashAttr("patient", invalidPatientDTO))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("patient-form"))
                .andExpect(model().attributeExists("newPatientError", "patient"));
    }

    @Test
    public void getEditPatientForm_shouldReturnEditForm() throws Exception {

        Patient patient = new Patient();
        patient.setFirstname("firstname");
        when(patientService.getPatient(anyInt())).thenReturn(patient);

        mockMvc.perform(get("/patients/edit/1"))
                .andExpect(view().name("update-patient"))
                .andExpect(model().attributeExists("patient"))
                .andExpect(content().string(containsString("firstname")));

        verify(patientService).getPatient(anyInt());
    }

    @Test
    public void getEditPatientForm_withNoPatient_shouldReturnError() throws Exception {

        when(patientService.getPatient(anyInt())).thenReturn(null);

        mockMvc.perform(get("/patients/edit/1"))
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("status", "error", "message"));

        verify(patientService).getPatient(anyInt());
    }

    @Test
    public void updatePatient_shouldUpdateAndRedirect() throws Exception {

        int id = 1;
        when(patientService.updatePatient(anyInt(), any(Patient.class))).thenReturn(true);

        mockMvc.perform(post("/patients/edit/" + id).flashAttr("patient", validPatient))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients/" + id));
    }

    @Test
    public void updatePatient_withError_shouldReturnFormWithError() throws Exception {

        int id = 1;
        when(patientService.updatePatient(anyInt(), any(Patient.class))).thenReturn(false);

        mockMvc.perform(post("/patients/edit/" + id).flashAttr("patient", validPatient))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("update-patient"))
                .andExpect(model().attributeExists("updatePatientError", "patient"));

        verify(patientService).updatePatient(anyInt(), any(Patient.class));
    }

    @ParameterizedTest
    @MethodSource("invalidPatientProvider")
    public void updatePatient_withInvalidPatient_shouldReturnFormWithError(Patient invalidPatient) throws Exception {

        int id = 1;

        mockMvc.perform(post("/patients/edit/" + id).flashAttr("patient", invalidPatient))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("update-patient"))
                .andExpect(model().attributeExists("updatePatientError", "patient"));
    }

    @Test
    public void deletePatient_shouldDeleteAndRedirect() throws Exception {

        when(patientService.deletePatient(anyInt())).thenReturn(true);

        mockMvc.perform(post("/patients/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));

        verify(patientService).deletePatient(anyInt());
    }

    @Test
    public void deletePatient_withError_shouldReturnPatientsWithError() throws Exception {

        when(patientService.deletePatient(anyInt())).thenReturn(false);

        mockMvc.perform(post("/patients/delete/1"))
                .andExpect(view().name("patients"))
                .andExpect(model().attributeExists("deletePatientError", "patients"));

        verify(patientService).deletePatient(anyInt());
    }
}
