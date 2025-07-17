package medilabo.frontapp;

import medilabo.frontapp.config.CustomProperties;
import medilabo.frontapp.config.SecurityConfig;
import medilabo.frontapp.controller.PatientController;
import medilabo.frontapp.service.PatientService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
@Import({SecurityConfig.class, CustomProperties.class})
@ActiveProfiles("test")
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @ParameterizedTest
    @ValueSource(strings={"/patients", "/patients/1", "/patients/add","/patients/add/1"})
    @WithAnonymousUser
    public void getAnyPage_withAnonymousUser_shouldReturnUnauthorized(String args) throws Exception {

        mockMvc.perform(get(args))
                .andExpect(status().isUnauthorized());
    }
}
