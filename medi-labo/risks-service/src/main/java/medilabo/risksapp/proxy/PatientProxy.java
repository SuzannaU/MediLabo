package medilabo.risksapp.proxy;

import medilabo.risksapp.config.FeignConfig;
import medilabo.risksapp.model.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

/**
 * This interface uses Feign Client to build requests that are sent to the patients-service module, through the gateway.
 *
 * @see FeignConfig
 */
@Repository
@FeignClient(name = "gateway-service", contextId = "patients-service", configuration = FeignConfig.class)
public interface PatientProxy {

    @GetMapping("/patients-service/patients/{id}")
    ResponseEntity<Patient> getPatient(@PathVariable("id") int id);
}
