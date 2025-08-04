package medilabo.frontapp.IT;

import medilabo.frontapp.TestFeignException;
import medilabo.frontapp.config.SecurityConfig;
import medilabo.frontapp.model.Note;
import medilabo.frontapp.proxy.NoteProxy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class NoteControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private NoteProxy noteProxy;

    @Test
    @WithMockUser
    public void addNote_shouldRedirect() throws Exception {

        when(noteProxy.createNote(any(Note.class)))
                .thenReturn(new ResponseEntity<>(new Note(), HttpStatus.CREATED));

        mockMvc.perform(post("/notes/add")
                        .flashAttr("note", new Note())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients/0"));
    }

    @Test
    @WithMockUser
    public void addNote_withError_shouldReturnFormWithError() throws Exception {

        when(noteProxy.createNote(any(Note.class)))
                .thenThrow(new TestFeignException(500, "message"));

        mockMvc.perform(post("/notes/add")
                        .flashAttr("note", new Note())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("add-note"))
                .andExpect(model().attributeExists("note", "newNoteError"));
    }
}
