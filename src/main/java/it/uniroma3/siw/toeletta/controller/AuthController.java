package it.uniroma3.siw.toeletta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.toeletta.model.Utente;
import it.uniroma3.siw.toeletta.service.UtenteService;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UtenteService utenteService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errore", "Username o password non validi.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("utente", new Utente());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Utente utente,
                           @RequestParam String confermaPassword,
                           RedirectAttributes redirectAttributes) {
        if (!utente.getPassword().equals(confermaPassword)) {
            redirectAttributes.addFlashAttribute("errore", "Le password non coincidono.");
            return "redirect:/auth/register";
        }

        try {
            utenteService.registra(utente);
            redirectAttributes.addFlashAttribute("successo", "Registrazione completata. Ora puoi accedere.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errore", e.getMessage());
            return "redirect:/auth/register";
        }
    }

    @GetMapping("/accesso-negato")
    public String accessoNegato() {
        return "auth/accesso-negato";
    }
}