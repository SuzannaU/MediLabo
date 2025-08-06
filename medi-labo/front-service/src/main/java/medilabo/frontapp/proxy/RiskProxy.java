package medilabo.frontapp.proxy;

import medilabo.frontapp.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * This interface uses Feign Client to build requests that are sent to the risks-service module, through the gateway.
 *
 * @see FeignConfig
 */
@Repository
@FeignClient(name = "gateway-service", contextId = "risks-service", configuration = FeignConfig.class)
public interface RiskProxy {

    @GetMapping("/risks-service/risks/{id}")
    ResponseEntity<String> getRiskLevelByPatientId(@PathVariable("id") int patientId);
}
