package medilabo.risksapp.controller;

import medilabo.risksapp.service.RiskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RiskController {

    private final RiskService riskService;

    public RiskController(RiskService riskService) {
        this.riskService = riskService;
    }

    @GetMapping("/risks/{id}")
    public String getRisks(@PathVariable("id") int id) {
        return riskService.calculateRisk(id).toString();
    }
}
