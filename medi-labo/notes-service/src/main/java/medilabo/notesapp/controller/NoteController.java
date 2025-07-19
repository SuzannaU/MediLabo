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

@RestController
@RequestMapping("/notes")
public class NoteController {
    private static final Logger logger = LoggerFactory.getLogger(NoteController.class);

    public final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<List<Note>> getNotesByPatientId(@PathVariable("patientId") int patientId) {
        logger.info("GetMapping for /notes/{}", patientId);
        try {
            List<Note> notes = noteService.getNotesByPatientId(patientId);
            logger.info("getNotesByPatientId returns {} notes", notes.size());
            return new ResponseEntity<>(notes, HttpStatus.OK);
        } catch (NoteNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

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
