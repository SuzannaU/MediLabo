package medilabo.notesapp;

import medilabo.notesapp.exceptions.NoteNotFoundException;
import medilabo.notesapp.model.Note;
import medilabo.notesapp.repository.NoteRepository;
import medilabo.notesapp.service.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class NoteServiceTest {

    @Autowired
    private NoteService noteService;

    @MockitoBean
    private NoteRepository noteRepository;

    @Test
    public void getNotesByPatientId_shouldCallRepoAndReturnNotes() {

        when(noteRepository.findByPatientId(anyInt())).thenReturn((List.of(new Note())));

        List<Note> notes = noteService.getNotesByPatientId(1);

        assertEquals(1, notes.size());
        verify(noteRepository).findByPatientId(anyInt());
    }

    @Test
    public void getNotesByPatientId_withNoNotes_shouldThrow() {
        when(noteRepository.findByPatientId(anyInt())).thenReturn(new ArrayList<>());

        assertThrows(NoteNotFoundException.class, () -> noteService.getNotesByPatientId(1));

        verify(noteRepository).findByPatientId(anyInt());
    }

    @Test
    public void addNote_shouldCallRepoAndReturnNote() {

        Note note = new Note();
        note.setContent("content");
        when(noteRepository.save(any(Note.class))).thenReturn(note);

        Note savedNote = noteService.addNote(note);

        assertEquals(note.getContent(), savedNote.getContent());
        verify(noteRepository).save(any(Note.class));
    }

}
