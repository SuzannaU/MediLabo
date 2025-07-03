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
import org.springframework.validation.BindingResult;
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

    @GetMapping(value = {"/patients", "", "/"})
    public String getPatients(Model model) {
        logger.info("GetMapping for /patients");
        List<Patient> patients = patientService.getAllPatients();
        if (patients == null) {
            model.addAttribute("emptyListError", "Il n'y a aucun patient");
            return "patients";
        }
        model.addAttribute("patients", patients);
        return "patients";
    }

    @GetMapping("/patients/{id}")
    public String getPatient(@PathVariable("id") int id, Model model) {
        logger.info("GetMapping for /patients/{}", id);
        Patient patient = patientService.getPatient(id);
        if (patient == null) {
            model.addAttribute("status", 404);
            model.addAttribute("error", "Not found");
            model.addAttribute("message", "Le patient id : " + id + " n'existe pas.");
            return "error";
        }
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
    public String addPatient(@Valid @ModelAttribute("patient") PatientDTO patientDTO, BindingResult result, Model model) {
        logger.info("PostMapping for /patients");
        if (!result.hasErrors() && patientService.createPatient(patientDTO)) {
            return "redirect:/patients";
        } else {
            model.addAttribute("newPatientError", "Impossible de créer le Patient");
            model.addAttribute("patient", patientDTO);
            return "patient-form";
        }
    }

    @GetMapping("/patients/edit/{id}")
    public String getEditPatientForm(@PathVariable("id") int id, Model model) {
        logger.info("GetMapping for /patients/edit/{}", id);
        Patient patient = patientService.getPatient(id);
        if (patient == null) {
            model.addAttribute("status", 404);
            model.addAttribute("error", "Not found");
            model.addAttribute("message", "Le patient id : " + id + " n'existe pas.");
            return "error";
        }
        model.addAttribute("patient", patient);
        return "update-patient";
    }

    @PostMapping("/patients/edit/{id}")
    public String updatePatient(@PathVariable("id") int id, @Valid @ModelAttribute("patient") Patient patient, BindingResult result, Model model) {
        logger.info("PostMapping for /patients/edit/{}", id);
        if (!result.hasErrors() && patientService.updatePatient(id, patient)) {
            return "redirect:/patients/" + id;
        } else {
            model.addAttribute("updatePatientError", "Impossible de modifier le Patient");
            model.addAttribute("patient", patient);
            return "update-patient";
        }
    }

    @PostMapping("/patients/delete/{id}")
    public String deletePatient(@PathVariable("id") int id, Model model) {
        logger.info("PostMapping for /patients/delete/{}", id);
        if (patientService.deletePatient(id)) {
            return "redirect:/patients";
        } else {
            logger.error("Patient with id {} not found", id);
            List<Patient> patients = patientService.getAllPatients();
            model.addAttribute("patients", patients);
            model.addAttribute("deletePatientError", "Impossible de supprimer le Patient");
            return "patients";
        }
    }
}

