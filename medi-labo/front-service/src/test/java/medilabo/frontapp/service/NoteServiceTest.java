package medilabo.frontapp.service;

import medilabo.frontapp.TestFeignException;
import medilabo.frontapp.model.Note;
import medilabo.frontapp.proxy.NoteProxy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class NoteServiceTest {

    @MockitoBean
    private NoteProxy noteProxy;

    @Autowired
    private NoteService noteService;

    @Test
    public void getNotesByPatientId_shouldReturnNotes() {

        when(noteProxy.getNotesByPatientId(anyInt()))
                .thenReturn(new ResponseEntity<>(List.of(new Note()), HttpStatus.OK));

        List<Note> notes = noteService.getNotesByPatientId(1);

        assertEquals(1, notes.size());
        verify(noteProxy).getNotesByPatientId(anyInt());
    }

    @Test
    public void getNotesByPatientId_withNoContent_shouldReturnEmptyNotes() {

        when(noteProxy.getNotesByPatientId(anyInt()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        List<Note> notes = noteService.getNotesByPatientId(1);

        assertTrue(notes.isEmpty());
        verify(noteProxy).getNotesByPatientId(anyInt());
    }

    @Test
    public void getNotesByPatientId_withClientBadCode_shouldReturnNull() {

        when(noteProxy.getNotesByPatientId(anyInt()))
                .thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        List<Note> notes = noteService.getNotesByPatientId(1);

        assertNull(notes);
        verify(noteProxy).getNotesByPatientId(anyInt());
    }

    @Test
    public void getNotesByPatientId_withException_shouldReturnNull() {

        when(noteProxy.getNotesByPatientId(anyInt()))
                .thenThrow(new TestFeignException(500, "message"));

        List<Note> notes = noteService.getNotesByPatientId(1);

        assertNull(notes);
        verify(noteProxy).getNotesByPatientId(anyInt());
    }

    @Test
    public void addNote_shouldReturnTrue() {

        when(noteProxy.createNote(any(Note.class)))
                .thenReturn(new ResponseEntity<>(new Note(), HttpStatus.CREATED));

        assertTrue(noteService.addNote(new Note()));
        verify(noteProxy).createNote(any(Note.class));
    }

    @Test
    public void addNote_WithClientBadCode_shouldReturnFalse() {

        when(noteProxy.createNote(any(Note.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        assertFalse(noteService.addNote(new Note()));
        verify(noteProxy).createNote(any(Note.class));
    }

    @Test
    public void addNote_WithException_shouldReturnFalse() {

        when(noteProxy.createNote(any(Note.class)))
                .thenThrow(new TestFeignException(500, "message"));

        assertFalse(noteService.addNote(new Note()));
        verify(noteProxy).createNote(any(Note.class));
    }
}
