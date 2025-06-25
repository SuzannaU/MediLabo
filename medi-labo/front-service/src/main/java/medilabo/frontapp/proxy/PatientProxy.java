package medilabo.frontapp.proxy;

import medilabo.frontapp.model.Patient;
import medilabo.frontapp.model.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Repository
@FeignClient(name = "gateway-service")
public interface PatientProxy {

    @GetMapping("/patients-service/patients")
    List<Patient> getAllPatients();

    @GetMapping("/patients-service/patients/{id}")
    Patient getPatient(@PathVariable("id") int id);

    @PostMapping("/patients-service/patients")
    Patient createPatient(@RequestBody PatientDTO patient);

    @PutMapping("/patients-service/patients/{id}")
    Patient updatePatient(@PathVariable("id") int id, @RequestBody Patient patient);

    @DeleteMapping("/patients-service/patients/{id}")
    void deletePatient(@PathVariable("id") int id);
}
