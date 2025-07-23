package medilabo.frontapp.controller;

import medilabo.frontapp.config.SecurityConfig;
import medilabo.frontapp.model.Note;
import medilabo.frontapp.service.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@WebMvcTest(NoteController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    @Test
    @WithMockUser
    public void getAddNoteForm_shouldReturnForm() throws Exception {

        mockMvc.perform(get("/notes/add").param("id", "1"))
                .andExpect(view().name("add-note"))
                .andExpect(model().attributeExists("note"));
    }

    @Test
    @WithMockUser
    public void addNote_shouldRedirect() throws Exception {

        when(noteService.addNote(any(Note.class))).thenReturn(true);

        mockMvc.perform(post("/notes/add")
                        .flashAttr("note", new Note())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients/0"));
    }

    @Test
    @WithMockUser
    public void addNote_withError_shouldReturnFormWithError() throws Exception {

        when(noteService.addNote(any(Note.class))).thenReturn(false);

        mockMvc.perform(post("/notes/add")
                        .flashAttr("note", new Note())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("add-note"))
                .andExpect(model().attributeExists("note", "newNoteError"));
    }
}
