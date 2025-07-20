package medilabo.notesapp.service;

import medilabo.notesapp.exceptions.NoteNotFoundException;
import medilabo.notesapp.model.Note;
import medilabo.notesapp.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service that performs operations on Note objects. Its methods are called by NoteController, and it calls NoteRepository to perform operations on the databse.
 *
 * @see medilabo.notesapp.controller.NoteController
 * @see NoteRepository
 */
@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    /**
     * Gets all Notes according to one patient ID
     *
     * @param patientId
     * @return a List of Notes
     * @throws NoteNotFoundException if no note has this ID as patientID attribute
     */
    public List<Note> getNotesByPatientId(int patientId) {
        List<Note> notes = noteRepository.findByPatientId(patientId);
        if (notes.isEmpty()) {
            throw new NoteNotFoundException("No notes found for patient id: " + patientId);
        }
        return notes;
    }

    /**
     * Adds a new Note.
     *
     * @param note
     * @return the saved note
     */
    public Note addNote(Note note) {
        return noteRepository.save(note);
    }
}
