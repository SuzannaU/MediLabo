package medilabo.frontapp;

import medilabo.frontapp.config.CustomProperties;
import medilabo.frontapp.config.SecurityConfig;
import medilabo.frontapp.controller.PatientController;
import medilabo.frontapp.model.Patient;
import medilabo.frontapp.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@Import({SecurityConfig.class, CustomProperties.class})
@ActiveProfiles("test")
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    private Patient validPatient;

    private final String username="${medilabo.user.username}";
    private final String password="${medilabo.user.password}";

    @BeforeEach
    public void beforeEach() {
        validPatient = new Patient("firstname", "lastname", LocalDate.now(), "g");

    }

    private static Stream<Arguments> invalidPatientProvider() {
        return Stream.of(
                Arguments.of(new Patient(null, "lastname", LocalDate.now(), "g")),
                Arguments.of(new Patient("firstname", null, LocalDate.now(), "g")),
                Arguments.of(new Patient("firstname", "lastname", null, "g")),
                Arguments.of(new Patient("firstname", "lastname", LocalDate.now(), null)),
                Arguments.of(new Patient("", "lastname", LocalDate.now(), "g")),
                Arguments.of(new Patient("firstname", "", LocalDate.now(), "g")),
                Arguments.of(new Patient("firstname", "lastname", LocalDate.now(), "")),
                Arguments.of(new Patient("firstname", "lastname", LocalDate.now(), "gender")));
    }

    @Test
    @WithMockUser(username=username, password=password)
    public void getPatients_shouldReturnPatients() throws Exception {

        when(patientService.getAllPatients()).thenReturn(List.of(new Patient()));

        mockMvc.perform(get("/patients"))
                .andExpect(view().name("patients"))
                .andExpect(model().attributeExists("patients"));

        verify(patientService).getAllPatients();
    }

    @Test
    @WithMockUser(username=username, password=password)
    public void getPatients_withNoPatients_shouldReturnPatientsWithError() throws Exception {

        when(patientService.getAllPatients()).thenReturn(null);

        mockMvc.perform(get("/patients"))
                .andExpect(view().name("patients"))
                .andExpect(model().attributeDoesNotExist("patients"))
                .andExpect(model().attributeExists("emptyListError"));

        verify(patientService).getAllPatients();
    }

    @Test
    @WithMockUser(username=username, password=password)
    public void getPatient_shouldReturnPatientDetails() throws Exception {

        when(patientService.getPatient(anyInt())).thenReturn(new Patient());

        mockMvc.perform(get("/patients/1"))
                .andExpect(view().name("patient-details"))
                .andExpect(model().attributeExists("patient"));

        verify(patientService).getPatient(anyInt());
    }

    @Test
    @WithMockUser(username=username, password=password)
    public void getPatient_withNoPatient_shouldReturnError() throws Exception {

        when(patientService.getPatient(anyInt())).thenReturn(null);

        mockMvc.perform(get("/patients/1"))
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("status", "error", "message"));

        verify(patientService).getPatient(anyInt());
    }

    @Test
    @WithMockUser(username=username, password=password)
    public void getNewPatientForm_shouldReturnForm() throws Exception {

        mockMvc.perform(get("/patients/add"))
                .andExpect(view().name("add-patient"));
    }

    @Test
    @WithMockUser(username=username, password=password)
    public void addPatient_shouldSAveAndRedirect() throws Exception {

        when(patientService.createPatient(any(Patient.class))).thenReturn(true);

        mockMvc.perform(post("/patients/add")
                        .flashAttr("patient", validPatient)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));

        verify(patientService).createPatient(any(Patient.class));
    }

    @Test
    @WithMockUser(username=username, password=password)
    public void addPatient_withError_shouldReturnFormWithError() throws Exception {

        when(patientService.createPatient(any(Patient.class))).thenReturn(false);

        mockMvc.perform(post("/patients/add")
                        .flashAttr("patient", validPatient)
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("add-patient"))
                .andExpect(model().attributeExists("newPatientError", "patient"));

        verify(patientService).createPatient(any(Patient.class));
    }

    @ParameterizedTest
    @MethodSource("invalidPatientProvider")
    @WithMockUser(username=username, password=password)
    public void addPatient_withInvalidPatient_shouldReturnFormWithError(Patient invalidPatient) throws Exception {

        mockMvc.perform(post("/patients/add")
                        .flashAttr("patient", invalidPatient)
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("add-patient"))
                .andExpect(model().attributeExists("newPatientError", "patient"));
    }

    @Test
    @WithMockUser(username=username, password=password)
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
    @WithMockUser(username=username, password=password)
    public void getEditPatientForm_withNoPatient_shouldReturnError() throws Exception {

        when(patientService.getPatient(anyInt())).thenReturn(null);

        mockMvc.perform(get("/patients/edit/1"))
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("status", "error", "message"));

        verify(patientService).getPatient(anyInt());
    }

    @Test
    @WithMockUser(username=username, password=password)
    public void updatePatient_shouldUpdateAndRedirect() throws Exception {

        int id = 1;
        when(patientService.updatePatient(anyInt(), any(Patient.class))).thenReturn(true);

        mockMvc.perform(post("/patients/edit/" + id)
                        .flashAttr("patient", validPatient)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients/" + id));
    }

    @Test
    @WithMockUser(username=username, password=password)
    public void updatePatient_withError_shouldReturnFormWithError() throws Exception {

        int id = 1;
        when(patientService.updatePatient(anyInt(), any(Patient.class))).thenReturn(false);

        mockMvc.perform(post("/patients/edit/" + id)
                        .flashAttr("patient", validPatient)
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("update-patient"))
                .andExpect(model().attributeExists("updatePatientError", "patient"));

        verify(patientService).updatePatient(anyInt(), any(Patient.class));
    }

    @ParameterizedTest
    @MethodSource("invalidPatientProvider")
    @WithMockUser(username=username, password=password)
    public void updatePatient_withInvalidPatient_shouldReturnFormWithError(Patient invalidPatient) throws Exception {

        int id = 1;

        mockMvc.perform(post("/patients/edit/" + id)
                        .flashAttr("patient", invalidPatient)
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("update-patient"))
                .andExpect(model().attributeExists("updatePatientError", "patient"));
    }

    @Test
    @WithMockUser(username=username, password=password)
    public void deletePatient_shouldDeleteAndRedirect() throws Exception {

        when(patientService.deletePatient(anyInt())).thenReturn(true);

        mockMvc.perform(post("/patients/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));

        verify(patientService).deletePatient(anyInt());
    }

    @Test
    @WithMockUser(username=username, password=password)
    public void deletePatient_withError_shouldReturnPatientsWithError() throws Exception {

        when(patientService.deletePatient(anyInt())).thenReturn(false);

        mockMvc.perform(post("/patients/delete/1")
                        .with(csrf()))
                .andExpect(view().name("patients"))
                .andExpect(model().attributeExists("deletePatientError", "patients"));

        verify(patientService).deletePatient(anyInt());
    }
}
