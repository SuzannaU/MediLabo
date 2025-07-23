package medilabo.risksapp;

import medilabo.risksapp.config.SecurityConfig;
import medilabo.risksapp.controller.RiskController;
import medilabo.risksapp.exceptions.NotesNotFoundException;
import medilabo.risksapp.exceptions.PatientNotFoundException;
import medilabo.risksapp.model.RiskLevel;
import medilabo.risksapp.service.RiskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RiskController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class RiskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RiskService riskService;

    @Test
    @WithMockUser
    public void getRiskLevelByPatientId_shouldReturnRiskAndOk() throws Exception {

        when(riskService.calculateRisk(anyInt())).thenReturn(RiskLevel.NONE);

        MvcResult result = mockMvc
                .perform(get("/risks/1"))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertEquals(RiskLevel.NONE.toString(), resultContent);
        verify(riskService).calculateRisk(anyInt());
    }

    @Test
    @WithMockUser
    public void getRiskLevelByPatientId_withNotesException_shouldReturnNAAndOk() throws Exception {

        when(riskService.calculateRisk(anyInt())).thenThrow(new NotesNotFoundException());

        MvcResult result = mockMvc
                .perform(get("/risks/1"))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertEquals(RiskLevel.NOT_APPLICABLE.toString(), resultContent);
        verify(riskService).calculateRisk(anyInt());
    }

    @Test
    @WithMockUser
    public void getRiskLevelByPatientId_withPatientException_shouldReturnNotFound() throws Exception {

        when(riskService.calculateRisk(anyInt())).thenThrow(new PatientNotFoundException());

        MvcResult result = mockMvc
                .perform(get("/risks/1"))
                .andExpect(status().isNotFound())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertTrue(resultContent.isEmpty());
        verify(riskService).calculateRisk(anyInt());
    }

    @Test
    @WithMockUser
    public void getRiskLevelByPatientId_withException_shouldReturnServerError() throws Exception {

        when(riskService.calculateRisk(anyInt())).thenThrow(new RuntimeException());

        MvcResult result = mockMvc
                .perform(get("/risks/1"))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertTrue(resultContent.isEmpty());
        verify(riskService).calculateRisk(anyInt());
    }

}
