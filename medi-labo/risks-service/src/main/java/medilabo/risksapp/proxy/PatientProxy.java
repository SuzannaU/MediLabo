package medilabo.risksapp.proxy;

import medilabo.risksapp.config.FeignConfig;
import medilabo.risksapp.model.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@Repository
@FeignClient(name = "gateway-service", contextId = "patients-service",  configuration = FeignConfig.class)
public interface PatientProxy {

    @GetMapping("/patients-service/patients/{id}")
    ResponseEntity<Patient> getPatient(@PathVariable("id") int id);
}
