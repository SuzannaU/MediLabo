package medilabo.frontapp.proxy;

import medilabo.frontapp.config.FeignConfig;
import medilabo.frontapp.model.Note;
import medilabo.frontapp.model.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Repository
@FeignClient(name = "gateway-service", contextId = "patients-service",  configuration = FeignConfig.class)
public interface PatientProxy {

    @GetMapping("/patients-service/patients")
    ResponseEntity<List<Patient>> getAllPatients();

    @GetMapping("/patients-service/patients/{id}")
    ResponseEntity<Patient> getPatient(@PathVariable("id") int id);

    @PostMapping("/patients-service/patients")
    ResponseEntity<Patient> createPatient(@RequestBody Patient patient);

    @PutMapping("/patients-service/patients/{id}")
    ResponseEntity<Patient> updatePatient(@PathVariable("id") int id, @RequestBody Patient patient);

    @DeleteMapping("/patients-service/patients/{id}")
    ResponseEntity<Patient> deletePatient(@PathVariable("id") int id);
}
