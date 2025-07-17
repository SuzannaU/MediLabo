package medilabo.frontapp;

import medilabo.frontapp.config.CustomProperties;
import medilabo.frontapp.config.SecurityConfig;
import medilabo.frontapp.controller.HomeController;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(HomeController.class)
@Import({SecurityConfig.class, CustomProperties.class})
@ActiveProfiles("test")
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings={"", "/"})
    public void getHome_shouldReturnHome(String args) throws Exception {
        mockMvc.perform(get(args))
                .andExpect(view().name("home"));
    }
}
