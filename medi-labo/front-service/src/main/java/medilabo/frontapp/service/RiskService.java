package medilabo.frontapp.service;

import feign.FeignException;
import medilabo.frontapp.proxy.RiskProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service used to handle Response entities received from the proxy interface, regarding RiskLevel String objects.
 *
 * @see RiskProxy
 */
@Service
public class RiskService {
    private final Logger logger = LoggerFactory.getLogger(RiskService.class);

    private final RiskProxy riskProxy;
    private final Map<Integer, String> risksCache = new ConcurrentHashMap<>();

    public RiskService(RiskProxy riskProxy) {
        this.riskProxy = riskProxy;
    }

    /**
     * Checks in riskCache Map if an entry already exists with that ID, so that the risk won't be calculated if it already has been. If the ID is absent from the Map, then retrieves the RiskLevel for that ID and adds it to the cache Map.
     *
     * @param patientId
     * @return risk level String if successful, null if an error is encountered.
     */
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
