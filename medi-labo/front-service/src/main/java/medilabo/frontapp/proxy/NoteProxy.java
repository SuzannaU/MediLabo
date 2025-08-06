package medilabo.frontapp.proxy;

import medilabo.frontapp.config.FeignConfig;
import medilabo.frontapp.model.Note;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * This interface uses Feign Client to build requests that are sent to the notes-service module, through the gateway.
 *
 * @see FeignConfig
 */
@Repository
@FeignClient(name = "gateway-service", contextId = "notes-service", configuration = FeignConfig.class)
public interface NoteProxy {

    @GetMapping("/notes-service/notes/{patientId}")
    ResponseEntity<List<Note>> getNotesByPatientId(@PathVariable("patientId") int patientId);

    @PostMapping("/notes-service/notes")
    ResponseEntity<Note> createNote(@RequestBody Note note);

}
