package medilabo.risksapp.controller;

import medilabo.risksapp.exceptions.NotesNotFoundException;
import medilabo.risksapp.exceptions.PatientNotFoundException;
import medilabo.risksapp.model.RiskLevel;
import medilabo.risksapp.service.RiskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RiskController {
    private final Logger logger = LoggerFactory.getLogger(RiskController.class);

    private final RiskService riskService;

    public RiskController(RiskService riskService) {
        this.riskService = riskService;
    }

    @GetMapping("/risks/{id}")
    public ResponseEntity<String> getRiskLevelByPatientId(@PathVariable("id") int id) {
        try {
            String riskLevel = riskService.calculateRisk(id).toString();
            return new ResponseEntity<>(riskLevel, HttpStatus.OK);
        } catch (NotesNotFoundException e) {
            return new ResponseEntity<>(RiskLevel.NOT_APPLICABLE.toString(), HttpStatus.OK);
        } catch (PatientNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
