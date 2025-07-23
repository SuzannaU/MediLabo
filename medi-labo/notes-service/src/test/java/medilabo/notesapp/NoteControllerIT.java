package medilabo.notesapp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import medilabo.notesapp.config.SecurityConfig;
import medilabo.notesapp.model.Note;
import medilabo.notesapp.repository.NoteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class NoteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NoteRepository noteRepository;

    private List<Note> backup;

    @BeforeEach
    public void beforeEach() {
        backup = noteRepository.findAll();
    }

    @AfterEach
    public void afterEach() {
        noteRepository.deleteAll();
        noteRepository.insert(backup);
    }

    @Test
    @WithMockUser
    public void getNotesByPatientId_shouldReturnNotesAndOk() throws Exception {

        MvcResult result = mockMvc
                .perform(get("/notes/1"))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        List<Note> resultNotes = new ObjectMapper()
                .readValue(resultContent, new TypeReference<List<Note>>() {
                });

        assertEquals(1, resultNotes.size());
    }

    @Test
    @WithMockUser
    public void getNotesByPatientId_withNoNotes_shouldReturnNoContent() throws Exception {

        MvcResult result = mockMvc
                .perform(get("/notes/123456789"))
                .andExpect(status().isNoContent())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();

        assertTrue(resultContent.isEmpty());
    }

    @Test
    @WithMockUser
    public void addNote_shouldReturnNoteAndCreated() throws Exception {
        Note note = new Note();
        note.setContent("content");

        MvcResult result = mockMvc
                .perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(note)))
                .andExpect(status().isCreated())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        Note resultNote =new ObjectMapper().readValue(resultContent,Note.class);

        assertEquals(note.getContent(), resultNote.getContent());
    }
}
