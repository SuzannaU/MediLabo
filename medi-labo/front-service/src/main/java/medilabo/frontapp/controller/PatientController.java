package medilabo.frontapp.controller;

import jakarta.validation.Valid;
import medilabo.frontapp.config.CustomProperties;
import medilabo.frontapp.model.Patient;
import medilabo.frontapp.model.PatientDTO;
import medilabo.frontapp.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PatientController {
    private final Logger logger = LoggerFactory.getLogger(PatientController.class);

    private final CustomProperties customProperties;
    private final PatientService patientService;

    public PatientController(CustomProperties customProperties, PatientService patientService) {
        this.customProperties = customProperties;
        this.patientService = patientService;
    }

    @GetMapping(value={"/patients","", "/"})
    public String getPatients (Model model) {
        logger.info("GetMapping for /patients");
        List<Patient> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);
        return "patients";
    }

    @GetMapping("/patients/{id}")
    public String getPatient (@PathVariable("id") int id, Model model) {
        logger.info("GetMapping for /patients/{}", id);
        Patient patient = patientService.getPatient(id);
        model.addAttribute("patient", patient);
        return "patient-details";
    }

    @GetMapping("/patients/add")
    public String getNewPatientForm(Model model) {
        logger.info("GetMapping for /patients/add");
        model.addAttribute("patient", new PatientDTO());
        return "patient-form";
    }

    @PostMapping("/patients/add")
    public String addPatient(@Valid @ModelAttribute("patient") PatientDTO patient) {
        logger.info("PostMapping for /patients");
        patientService.createPatient(patient);
        return "redirect:" + getProxiedServiceUrl() + "/patients";
    }

    @GetMapping("/patients/edit/{id}")
    public String getEditPatientForm(@PathVariable("id") int id, Model model) {
        logger.info("GetMapping for /patients/edit/{}", id);
        Patient patient = patientService.getPatient(id);
        model.addAttribute("patient", patient);
        return "update-patient";
    }

    @PostMapping("/patients/edit/{id}")
    public String updatePatient (@PathVariable("id") int id, @Valid @ModelAttribute("patient") PatientDTO patientDTO) {
        logger.info("PostMapping for /patients/edit/{}", id);
        logger.info("Update patient: {}", patientDTO);
        patientService.updatePatient(id, patientDTO);
        return "redirect:" + getProxiedServiceUrl() + "/patients/" + id;
    }

    @PostMapping("/patients/delete/{id}")
    public String deletePatient (@PathVariable("id") int id) {
        logger.info("PostMapping for /patients/delete/{}", id);
        patientService.deletePatient(id);
        return "redirect:" + getProxiedServiceUrl() + "/patients";
    }

    private String getProxiedServiceUrl(){
        String gatewayUrl= customProperties.getGatewayUrl();
        return gatewayUrl + "/front-service";
    }
}
