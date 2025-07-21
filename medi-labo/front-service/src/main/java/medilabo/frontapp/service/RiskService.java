package medilabo.frontapp.service;

import feign.FeignException;
import medilabo.frontapp.proxy.RiskProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RiskService {
    private final Logger logger = LoggerFactory.getLogger(RiskService.class);

    private final RiskProxy riskProxy;

    public RiskService(RiskProxy riskProxy) {
        this.riskProxy = riskProxy;
    }

    public String getRiskByPatientId(int patientId) {
        try {
            ResponseEntity<String> response = riskProxy.getRiskLevelByPatientId(patientId);
            String riskLevel = response.getBody();
            int statusCode = response.getStatusCode().value();
            if (statusCode == 200) {
                return riskLevel;
            }
            logger.error("Problem retrieving risk level. Error: {} ", statusCode);
            return null;
        } catch (FeignException e) {
            logger.error("Problem retrieving risk level.", e);
            return null;
        }
    }
}
