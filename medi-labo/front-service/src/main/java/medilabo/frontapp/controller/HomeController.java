package medilabo.frontapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    /**
     * The "" and "/" mappings return the home template. No authentication is required.
     *
     * @return home template
     */
    @GetMapping(path = {"", "/"})
    public String getHome() {
        return "home";
    }
}
