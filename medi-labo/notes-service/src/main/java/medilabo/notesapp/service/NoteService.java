package medilabo.notesapp.service;

import medilabo.notesapp.exceptions.NoteNotFoundException;
import medilabo.notesapp.model.Note;
import medilabo.notesapp.repository.NoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<Note> getNotesByPatientId(int patientId) {
        List<Note> notes = noteRepository.findByPatientId(patientId);
        if (notes.isEmpty()) {
            throw new NoteNotFoundException("No notes found for patient id: " + patientId);
        }
        return notes;
    }

    public Note addNote(Note note) {
        return noteRepository.save(note);
    }
}
