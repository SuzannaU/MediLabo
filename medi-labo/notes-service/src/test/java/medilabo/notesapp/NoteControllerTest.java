package medilabo.notesapp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import medilabo.notesapp.config.CustomProperties;
import medilabo.notesapp.config.SecurityConfig;
import medilabo.notesapp.controller.NoteController;
import medilabo.notesapp.exceptions.NoteNotFoundException;
import medilabo.notesapp.model.Note;
import medilabo.notesapp.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NoteController.class)
@ActiveProfiles("test")
@Import({SecurityConfig.class, CustomProperties.class})
public class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    private Note note;

    private final String username="${medilabo.user.username}";
    private final String password="${medilabo.user.password}";

    @BeforeEach
    public void beforeEach() {
        note = new Note();
        note.setPatientId(1);
    }

    @Test
    @WithMockUser(username=username, password=password)
    public void getNotesByPatientId_shouldReturnNotesAndOk() throws Exception {
        when(noteService.getNotesByPatientId(anyInt())).thenReturn(List.of(note));

        MvcResult result = mockMvc
                .perform(get("/notes/1"))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        List<Note> resultNotes = new ObjectMapper()
                .readValue(resultContent, new TypeReference<List<Note>>() {
                });

        assertEquals(1, resultNotes.size());
        verify(noteService).getNotesByPatientId(anyInt());
    }

    @Test
    @WithMockUser(username=username, password=password)
    public void getNotesByPatientId_withNoNotesException_shouldReturnNoContent() throws Exception {
        when(noteService.getNotesByPatientId(anyInt())).thenThrow(new NoteNotFoundException());

        MvcResult result = mockMvc
                .perform(get("/notes/1"))
                .andExpect(status().isNoContent())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertTrue(resultContent.isEmpty());
        verify(noteService).getNotesByPatientId(anyInt());
    }

    @Test
    @WithMockUser(username=username, password=password)
    public void addNote_shouldReturnNoteAndCreated() throws Exception {
        when(noteService.addNote(any(Note.class))).thenReturn(note);

        MvcResult result = mockMvc
                .perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(note)))
                .andExpect(status().isCreated())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        Note resultNote =new ObjectMapper().readValue(resultContent,Note.class);

        assertEquals(note.getPatientId(), resultNote.getPatientId());
        verify(noteService).addNote(any(Note.class));
    }

    @Test
    @WithMockUser(username=username, password=password)
    public void addNote_withException_shouldReturnServerError() throws Exception {
        when(noteService.addNote(any(Note.class))).thenThrow(new RuntimeException());

        MvcResult result = mockMvc
                .perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(note)))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertTrue(resultContent.isEmpty());
        verify(noteService).addNote(any(Note.class));
    }
}
