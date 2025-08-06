package medilabo.risksapp.proxy;

import medilabo.risksapp.config.FeignConfig;
import medilabo.risksapp.model.Note;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
}
