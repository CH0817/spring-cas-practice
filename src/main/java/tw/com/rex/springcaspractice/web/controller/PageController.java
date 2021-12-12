package tw.com.rex.springcaspractice.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @Value("${server.port}")
    private String port;

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        model.addAttribute("port", port);
        return "index";
    }

}
