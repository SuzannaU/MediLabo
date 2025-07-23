package medilabo.risksapp;

import feign.FeignException;
import medilabo.risksapp.exceptions.NotesNotFoundException;
import medilabo.risksapp.model.Note;
import medilabo.risksapp.proxy.NoteProxy;
import medilabo.risksapp.service.TriggerUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class TriggerUtilTest {

    @MockitoBean
    private NoteProxy noteProxy;

    @Autowired
    private TriggerUtil triggerUtil;

    private List<Note> notes;

    @BeforeEach
    public void BeforeEach() {
        notes = new ArrayList<>();
        Note note1 = new Note("rechute, fumer");    // fumer is not a trigger
        Note note2 = new Note("vertige, ANORMALES");
        notes.add(note1);
        notes.add(note2);
    }

    @Test
    public void countMatchingTriggers_shouldReturnCount() {

        when(noteProxy.getNotesByPatientId(anyInt()))
                .thenReturn(new ResponseEntity<>(notes, HttpStatus.OK));

        int result = triggerUtil.countMatchingTriggers(1);

        assertEquals(3, result);
        verify(noteProxy).getNotesByPatientId(anyInt());
    }

    @Test
    public void countMatchingTriggers_withNot200StatusCode_shouldThrow() {

        when(noteProxy.getNotesByPatientId(anyInt()))
                .thenReturn(new ResponseEntity<>(notes, HttpStatus.NO_CONTENT));

        assertThrows(NotesNotFoundException.class, () -> triggerUtil.countMatchingTriggers(1));
        verify(noteProxy).getNotesByPatientId(anyInt());
    }

    @Test
    public void countMatchingTriggers_withException_shouldThrow() {

        when(noteProxy.getNotesByPatientId(anyInt()))
                .thenThrow(new TestFeignException());

        assertThrows(NotesNotFoundException.class, () -> triggerUtil.countMatchingTriggers(1));
        verify(noteProxy).getNotesByPatientId(anyInt());
    }

    private static class TestFeignException extends FeignException {
        protected TestFeignException() {
            super(500, "message");
        }
    }
}
