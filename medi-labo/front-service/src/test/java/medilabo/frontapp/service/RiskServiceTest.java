package medilabo.frontapp.service;

import medilabo.frontapp.TestFeignException;
import medilabo.frontapp.proxy.RiskProxy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class RiskServiceTest {

    @MockitoBean
    private RiskProxy riskProxy;

    @Autowired
    private RiskService riskService;

    @Test
    public void getRiskByPatientId_withOkCode_shouldReturnRisk() {

        when(riskProxy.getRiskLevelByPatientId(anyInt()))
                .thenReturn(new ResponseEntity<>("Risk Level", HttpStatus.OK));

        String result = riskService.getRiskByPatientId(1);

        assertEquals("Risk Level", result);
        verify(riskProxy).getRiskLevelByPatientId(anyInt());
    }

    @Test
    public void getRiskByPatientId_withNotOkCode_shouldReturnNull() {

        when(riskProxy.getRiskLevelByPatientId(anyInt()))
                .thenReturn(new ResponseEntity<>("Risk Level", HttpStatus.CREATED));

        String result = riskService.getRiskByPatientId(1);

        assertNull(result);
        verify(riskProxy).getRiskLevelByPatientId(anyInt());
    }

    @Test
    public void getRiskByPatientId_withException_shouldReturnNull() {

        when(riskProxy.getRiskLevelByPatientId(anyInt()))
                .thenThrow(new TestFeignException(500, "message"));

        String result = riskService.getRiskByPatientId(1);

        assertNull(result);
        verify(riskProxy).getRiskLevelByPatientId(anyInt());
    }
}
