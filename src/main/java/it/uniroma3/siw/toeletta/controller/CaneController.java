package it.uniroma3.siw.toeletta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.toeletta.model.Cane;
import it.uniroma3.siw.toeletta.model.TagliaCane;
import it.uniroma3.siw.toeletta.model.TipoPelo;
import it.uniroma3.siw.toeletta.model.Utente;
import it.uniroma3.siw.toeletta.service.CaneService;
import it.uniroma3.siw.toeletta.service.UtenteService;

@Controller
@RequestMapping("/cani")
public class CaneController {

    @Autowired
    private CaneService caneService;

    @Autowired
    private UtenteService utenteService;

    @GetMapping
    public String elenco(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Utente utente = utenteService.findByUsername(userDetails.getUsername());

        model.addAttribute("cani", caneService.findByProprietario(utente.getId()));
        return "cane/elenco";
    }

    @GetMapping("/nuovo")
    public String nuovoForm(Model model) {
        model.addAttribute("cane", new Cane());
        model.addAttribute("taglie", TagliaCane.values());
        model.addAttribute("tipiPelo", TipoPelo.values());
        return "cane/form";
    }

    @PostMapping("/nuovo")
    public String salva(@ModelAttribute Cane cane,
                        @AuthenticationPrincipal UserDetails userDetails,
                        RedirectAttributes redirectAttributes) {
        Utente utente = utenteService.findByUsername(userDetails.getUsername());

        caneService.registraCane(cane, utente.getId());

        redirectAttributes.addFlashAttribute("successo", "Cane registrato con successo.");
        return "redirect:/cani";
    }

    @GetMapping("/{id}/modifica")
    public String modificaForm(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        if (!caneService.appartieneAUsername(id, userDetails.getUsername())) {
            return "redirect:/auth/accesso-negato";
        }

        model.addAttribute("cane", caneService.findById(id));
        model.addAttribute("taglie", TagliaCane.values());
        model.addAttribute("tipiPelo", TipoPelo.values());
        return "cane/form";
    }

    @PostMapping("/{id}/modifica")
    public String modifica(@PathVariable Long id,
                           @ModelAttribute Cane cane,
                           @AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        if (!caneService.appartieneAUsername(id, userDetails.getUsername())) {
            return "redirect:/auth/accesso-negato";
        }

        caneService.update(id, cane);

        redirectAttributes.addFlashAttribute("successo", "Cane aggiornato.");
        return "redirect:/cani";
    }

    @PostMapping("/{id}/elimina")
    public String elimina(@PathVariable Long id,
                          @AuthenticationPrincipal UserDetails userDetails,
                          RedirectAttributes redirectAttributes) {
        if (!caneService.appartieneAUsername(id, userDetails.getUsername())) {
            return "redirect:/auth/accesso-negato";
        }

        caneService.delete(id);

        redirectAttributes.addFlashAttribute("successo", "Cane eliminato.");
        return "redirect:/cani";
    }
}