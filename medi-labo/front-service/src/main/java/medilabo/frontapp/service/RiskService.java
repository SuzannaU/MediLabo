package medilabo.frontapp.service;

import feign.FeignException;
import medilabo.frontapp.proxy.RiskProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RiskService {
    private final Logger logger = LoggerFactory.getLogger(RiskService.class);

    private final RiskProxy riskProxy;
    private final Map<Integer, String> risksCache = new ConcurrentHashMap<>();

    public RiskService(RiskProxy riskProxy) {
        this.riskProxy = riskProxy;
    }

    public String getRiskByPatientId(int patientId) {
        risksCache.computeIfAbsent(patientId, id -> {
            try {
                ResponseEntity<String> response = riskProxy.getRiskLevelByPatientId(patientId);
                String riskLevel = response.getBody();
                int statusCode = response.getStatusCode().value();
                if (statusCode == 200) {
                    logger.info("Risk level recovered successfully for patient {}", patientId);
                    return riskLevel;
                }
                logger.error("Problem retrieving risk level. Error: {} ", statusCode);
                return null;
            } catch (FeignException e) {
                logger.error("Problem retrieving risk level.", e);
                return null;
            }
        });
        return risksCache.get(patientId);
    }
}
