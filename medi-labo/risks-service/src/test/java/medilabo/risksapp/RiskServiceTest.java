package medilabo.risksapp;

import feign.FeignException;
import medilabo.risksapp.exceptions.PatientNotFoundException;
import medilabo.risksapp.model.Patient;
import medilabo.risksapp.model.RiskLevel;
import medilabo.risksapp.proxy.PatientProxy;
import medilabo.risksapp.service.RiskService;
import medilabo.risksapp.service.TriggerUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class RiskServiceTest {

    @MockitoBean
    private TriggerUtil triggerUtil;

    @MockitoBean
    private PatientProxy patientProxy;

    @Autowired
    private RiskService riskService;

    private Patient patient;

    @BeforeEach
    public void beforeEach() {
        patient = new Patient();
    }

    private static Stream<Arguments> patientProvider() {
        return Stream.of(
                // Age > 30
                Arguments.of(31, "M", 0, RiskLevel.NONE),
                Arguments.of(31, "F", 1, RiskLevel.NONE),
                Arguments.of(31, "F", 2, RiskLevel.BORDERLINE),
                Arguments.of(31, "F", 5, RiskLevel.BORDERLINE),
                Arguments.of(31, "F", 6, RiskLevel.IN_DANGER),
                Arguments.of(31, "F", 7, RiskLevel.IN_DANGER),
                Arguments.of(31, "F", 8, RiskLevel.EARLY_ONSET),
                Arguments.of(31, "F", 10, RiskLevel.EARLY_ONSET),

                // Age <= 30, gender M
                Arguments.of(30, "M", 0, RiskLevel.NONE),
                Arguments.of(30, "M", 2, RiskLevel.NONE),
                Arguments.of(30, "M", 3, RiskLevel.IN_DANGER),
                Arguments.of(30, "M", 4, RiskLevel.IN_DANGER),
                Arguments.of(30, "M", 5, RiskLevel.EARLY_ONSET),
                Arguments.of(30, "M", 6, RiskLevel.EARLY_ONSET),

                // Age <= 30, gender F
                Arguments.of(30, "F", 0, RiskLevel.NONE),
                Arguments.of(30, "F", 3, RiskLevel.NONE),
                Arguments.of(30, "F", 4, RiskLevel.IN_DANGER),
                Arguments.of(30, "F", 6, RiskLevel.IN_DANGER),
                Arguments.of(30, "F", 7, RiskLevel.EARLY_ONSET),
                Arguments.of(30, "F", 8, RiskLevel.EARLY_ONSET)
        );
    }

    @ParameterizedTest(name="age: {0}, gender: {1} and trigger count: {2} should return {3}")
    @MethodSource("patientProvider")
    public void calculateRisk_withPatientAndNotes_shouldReturnRiskLevel(
            int age, String gender, int triggers, RiskLevel expectedRiskLevel) {

        patient.setBirthdate(LocalDate.now().minusYears(age));
        patient.setGender(gender);

        when(patientProxy.getPatient(anyInt()))
                .thenReturn(new ResponseEntity<>(patient, HttpStatus.OK));

        when(triggerUtil.countMatchingTriggers(anyInt())).thenReturn(triggers);

        RiskLevel result = riskService.calculateRisk(1);

        assertEquals(expectedRiskLevel, result);
        verify(patientProxy).getPatient(anyInt());
        verify(triggerUtil).countMatchingTriggers(anyInt());
    }

    @Test
    public void calculateRisk_withNot200StatusCode_shouldThrow() {

        when(patientProxy.getPatient(anyInt()))
                .thenReturn(new ResponseEntity<>(patient, HttpStatus.NO_CONTENT));

        assertThrows(PatientNotFoundException.class, ()->riskService.calculateRisk(1));
        verify(patientProxy).getPatient(anyInt());
    }

    @Test
    public void calculateRisk_withException_shouldThrow() {
        when(patientProxy.getPatient(anyInt()))
                .thenThrow(new TestFeignException());

        assertThrows(PatientNotFoundException.class, ()->riskService.calculateRisk(1));
        verify(patientProxy).getPatient(anyInt());
    }

    private static class TestFeignException extends FeignException {
        protected TestFeignException() {
            super(500, "message");
        }
    }

}
