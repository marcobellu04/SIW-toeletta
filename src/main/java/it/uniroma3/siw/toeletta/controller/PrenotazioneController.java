package it.uniroma3.siw.toeletta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.toeletta.model.Utente;
import it.uniroma3.siw.toeletta.service.CaneService;
import it.uniroma3.siw.toeletta.service.FasciaOrariaService;
import it.uniroma3.siw.toeletta.service.PrenotazioneService;
import it.uniroma3.siw.toeletta.service.ServizioService;
import it.uniroma3.siw.toeletta.service.UtenteService;

@Controller
@RequestMapping("/prenotazioni")
public class PrenotazioneController {

    @Autowired
    private PrenotazioneService prenotazioneService;

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private CaneService caneService;

    @Autowired
    private ServizioService servizioService;

    @Autowired
    private FasciaOrariaService fasciaOrariaService;

    @GetMapping
    public String elenco(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Utente utente = utenteService.findByUsername(userDetails.getUsername());

        model.addAttribute("prenotazioniConfermate",
                prenotazioneService.findConfermateByUtente(utente.getId()));
        model.addAttribute("prenotazioniAnnullate",
                prenotazioneService.findAnnullateByUtente(utente.getId()));

        return "prenotazione/elenco";
    }

    @GetMapping("/nuova")
    public String nuova(@RequestParam(required = false) Long servizioId,
                        @AuthenticationPrincipal UserDetails userDetails,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        Utente utente = utenteService.findByUsername(userDetails.getUsername());

        if (caneService.findByProprietario(utente.getId()).isEmpty()) {
            redirectAttributes.addFlashAttribute("errore", "Prima di prenotare devi registrare almeno un cane.");
            return "redirect:/cani/nuovo";
        }

        model.addAttribute("cani", caneService.findByProprietario(utente.getId()));
        model.addAttribute("servizi", servizioService.findAttivi());
        model.addAttribute("fasceDisponibili", fasciaOrariaService.findDisponibili());
        model.addAttribute("servizioSelezionatoId", servizioId);

        return "prenotazione/form";
    }

    @PostMapping("/nuova")
    public String crea(@RequestParam Long caneId,
                       @RequestParam Long servizioId,
                       @RequestParam String fasciaScelta,
                       @RequestParam(required = false) String noteCliente,
                       @AuthenticationPrincipal UserDetails userDetails,
                       RedirectAttributes redirectAttributes) {
        Utente utente = utenteService.findByUsername(userDetails.getUsername());

        try {
            String[] parti = fasciaScelta.split("-");
            Long fasciaOrariaId = Long.valueOf(parti[0]);
            Long toelettatoreId = Long.valueOf(parti[1]);

            prenotazioneService.prenota(
                    utente.getId(),
                    caneId,
                    servizioId,
                    toelettatoreId,
                    fasciaOrariaId,
                    noteCliente
            );

            redirectAttributes.addFlashAttribute("successo", "Prenotazione confermata.");
            return "redirect:/prenotazioni";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errore", e.getMessage());
            return "redirect:/prenotazioni/nuova?servizioId=" + servizioId;
        }
    }

    @PostMapping("/{id}/annulla")
    public String annulla(@PathVariable Long id,
                          @AuthenticationPrincipal UserDetails userDetails,
                          RedirectAttributes redirectAttributes) {
        Utente utente = utenteService.findByUsername(userDetails.getUsername());

        try {
            prenotazioneService.annulla(id, utente.getId());
            redirectAttributes.addFlashAttribute("successo", "Prenotazione annullata.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errore", e.getMessage());
        }

        return "redirect:/prenotazioni";
    }
}