package medilabo.frontapp.controller;

import jakarta.servlet.http.HttpServletRequest;
import medilabo.frontapp.model.Note;
import medilabo.frontapp.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NoteController {
    private final Logger logger = LoggerFactory.getLogger(NoteController.class);

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/notes/add")
    public String getAddNoteForm(@RequestParam("id") int patientId, Model model) {
        logger.info("GetMapping for /notes/add");
        Note note = new Note();
        note.setPatientId(patientId);
        model.addAttribute("note", note);
        return "add-note";
    }

    @PostMapping("/notes/add")
    public String addNote(@ModelAttribute("note") Note note, BindingResult  result, Model model) {
        logger.info("PostMapping for /notes/add");
        if (!result.hasErrors() && noteService.addNote(note)) {
            return "redirect:/patients/" + note.getPatientId();
        } else {
            model.addAttribute("newNoteError", "Impossible de sauver la note");
            model.addAttribute("note", note);
            return "add-note";
        }
    }
}
