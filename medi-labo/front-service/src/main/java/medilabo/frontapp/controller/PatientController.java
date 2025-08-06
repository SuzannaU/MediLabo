package medilabo.frontapp.controller;

import jakarta.validation.Valid;
import medilabo.frontapp.model.Note;
import medilabo.frontapp.model.Patient;
import medilabo.frontapp.service.NoteService;
import medilabo.frontapp.service.PatientService;
import medilabo.frontapp.service.RiskService;
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

    private final PatientService patientService;
    private final NoteService noteService;
    private final RiskService riskService;

    public PatientController(PatientService patientService, NoteService noteService, RiskService riskService) {
        this.patientService = patientService;
        this.noteService = noteService;
        this.riskService = riskService;
    }

    /**
     * Displays the patients template populated with the list of patients. Model services request patient data from backend services.
     *
     * @param model
     * @return patients template
     */
    @GetMapping("/patients")
    public String getPatients(Model model) {
        logger.info("GetMapping for /patients");
        List<Patient> patients = patientService.getAllPatients();
        if (patients == null) {
            model.addAttribute("emptyListError", "Il n'y a aucun patient");
            return "patients";
        }

        for (Patient patient : patients) {
            String riskLevel = riskService.getRiskByPatientId(patient.getId());
            if (riskLevel != null) {
                patient.setRiskLevel(riskLevel);
            }
        }

        model.addAttribute("patients", patients);
        return "patients";
    }

    /**
     * Fetches the patient-details template and populates it with patient details gathered from backend services.
     *
     * @param id    the id of the desired patient
     * @param model
     * @return patient-details template if the patient is successfully retrieved, or error template with 404 status.
     */
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

        List<Note> notes = noteService.getNotesByPatientId(id);
        if (notes == null) {
            model.addAttribute("noNotesError", "Impossible de récupérer les notes");
        } else if (notes.isEmpty()) {
            model.addAttribute("noNotesError", "Aucune note pour ce patient");
        } else {
            patient.setNotes(notes);
        }

        String riskLevel = riskService.getRiskByPatientId(id);
        if (riskLevel == null) {
            model.addAttribute("riskError", "Impossible de calculer le risque");
        } else {
            patient.setRiskLevel(riskLevel);
        }

        model.addAttribute("patient", patient);
        return "patient-details";
    }

    /**
     * Fetches the add-patient template form for a new patient
     *
     * @param model
     * @return add-patient template
     */
    @GetMapping("/patients/add")
    public String getNewPatientForm(Model model) {
        logger.info("GetMapping for /patients/add");
        model.addAttribute("patient", new Patient());
        return "add-patient";
    }

    /**
     * Saves a new patient. Checks if validation requirements are met.
     *
     * @param patient data from the form entries
     * @param result
     * @param model
     * @return redirection to patients page if successful, or reloading of the form with error messages if not.
     */
    @PostMapping("/patients/add")
    public String addPatient(@Valid @ModelAttribute("patient") Patient patient, BindingResult result, Model model) {
        logger.info("PostMapping for /patients/add");
        if (!result.hasErrors() && patientService.createPatient(patient)) {
            return "redirect:/patients";
        } else {
            model.addAttribute("newPatientError", "Impossible de créer le Patient");
            model.addAttribute("patient", patient);
            return "add-patient";
        }
    }

    /**
     * Fetches the updating form and populates it with the data from the origin patient.
     *
     * @param id    the patient id
     * @param model
     * @return the update-patient template, or a 404 error page if no patient is found
     */
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

    /**
     * Updates a patient. Checks if validation requirements are met.
     *
     * @param id      the patient id
     * @param patient the data from the form entries
     * @param result
     * @param model
     * @return redirection to patients page if successful, or reloading of the page with error messages.
     */
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

    /**
     * Deletes a patient entirely, according to its id.
     *
     * @param id    the patient id
     * @param model
     * @return the patients page if deletion is successful, or reloading of the page with error messages if not.
     */
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

