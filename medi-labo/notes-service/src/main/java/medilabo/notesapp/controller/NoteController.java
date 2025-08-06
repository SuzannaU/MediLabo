package medilabo.notesapp.controller;

import medilabo.notesapp.exceptions.NoteNotFoundException;
import medilabo.notesapp.model.Note;
import medilabo.notesapp.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller that handles requests related to Note type. It calls methods from NoteService.
 * @see NoteService
 */
@RestController
@RequestMapping("/notes")
public class NoteController {
    private static final Logger logger = LoggerFactory.getLogger(NoteController.class);

    public final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Gets the List of all notes related to one patient ID
     * @param patientId
     * @return a ResponseEntity containing the List with 200 code, or with 204 if there are no notes related to this ID.
     */
    @GetMapping("/{patientId}")
    public ResponseEntity<List<Note>> getNotesByPatientId(@PathVariable("patientId") int patientId) {
        logger.info("GetMapping for /notes/{}", patientId);
        try {
            List<Note> notes = noteService.getNotesByPatientId(patientId);
            return new ResponseEntity<>(notes, HttpStatus.OK);
        } catch (NoteNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Creates a new Note.
     * @param note the new Note
     * @return a ReponseEntity with the saved note and 201 code, or ResponseEntity with 500 if an exception occurs
     */
    @PostMapping
    public ResponseEntity<Note> addNote(@RequestBody Note note) {
        logger.info("PostMapping for /notes");
        try {
            return new ResponseEntity<>(noteService.addNote(note), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Note was not created: {}", note.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
