package it.uniroma3.siw.toeletta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.siw.toeletta.service.ServizioService;

@Controller
public class ServizioController {

    @Autowired
    private ServizioService servizioService;

    @GetMapping("/servizi")
    public String elenco(Model model) {
        model.addAttribute("servizi", servizioService.findAttivi());
        return "servizio/elenco";
    }
}