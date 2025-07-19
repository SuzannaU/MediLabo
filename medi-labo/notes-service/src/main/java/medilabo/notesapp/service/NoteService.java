package medilabo.notesapp.service;

import medilabo.notesapp.model.Note;
import medilabo.notesapp.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<Note> getNotesByPatientId(int patientId) {
        return noteRepository.findByPatientId(patientId);
    }

    public Note addNote(Note note) {
        return noteRepository.save(note);
    }
}
