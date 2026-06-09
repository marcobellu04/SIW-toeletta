package it.uniroma3.siw.toeletta.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.toeletta.model.FasciaOraria;
import it.uniroma3.siw.toeletta.model.Servizio;
import it.uniroma3.siw.toeletta.model.Toelettatore;
import it.uniroma3.siw.toeletta.service.FasciaOrariaService;
import it.uniroma3.siw.toeletta.service.PrenotazioneService;
import it.uniroma3.siw.toeletta.service.ServizioService;
import it.uniroma3.siw.toeletta.service.ToelettatoreService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ServizioService servizioService;

    @Autowired
    private ToelettatoreService toelettatoreService;

    @Autowired
    private FasciaOrariaService fasciaOrariaService;

    @Autowired
    private PrenotazioneService prenotazioneService;

    @GetMapping
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/servizi")
    public String elencoServizi(Model model) {
        model.addAttribute("servizi", servizioService.findAll());
        return "admin/servizio/elenco";
    }

    @GetMapping("/servizi/nuovo")
    public String nuovoServizio(Model model) {
        model.addAttribute("servizio", new Servizio());
        return "admin/servizio/form";
    }

    @PostMapping("/servizi/nuovo")
    public String salvaServizio(@ModelAttribute Servizio servizio, RedirectAttributes redirectAttributes) {
        servizioService.save(servizio);
        redirectAttributes.addFlashAttribute("successo", "Servizio creato con successo.");
        return "redirect:/admin/servizi";
    }

    @GetMapping("/servizi/{id}/modifica")
    public String modificaServizio(@PathVariable Long id, Model model) {
        model.addAttribute("servizio", servizioService.findById(id));
        return "admin/servizio/form";
    }

    @PostMapping("/servizi/{id}/modifica")
    public String aggiornaServizio(@PathVariable Long id,
                                   @ModelAttribute Servizio servizio,
                                   RedirectAttributes redirectAttributes) {
        servizioService.update(id, servizio);
        redirectAttributes.addFlashAttribute("successo", "Servizio aggiornato.");
        return "redirect:/admin/servizi";
    }

    @PostMapping("/servizi/{id}/elimina")
    public String eliminaServizio(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        servizioService.delete(id);
        redirectAttributes.addFlashAttribute("successo", "Servizio eliminato.");
        return "redirect:/admin/servizi";
    }

    @GetMapping("/toelettatori")
    public String elencoToelettatori(Model model) {
        model.addAttribute("toelettatori", toelettatoreService.findAll());
        return "admin/toelettatore/elenco";
    }

    @GetMapping("/toelettatori/nuovo")
    public String nuovoToelettatore(Model model) {
        model.addAttribute("toelettatore", new Toelettatore());
        return "admin/toelettatore/form";
    }

    @PostMapping("/toelettatori/nuovo")
    public String salvaToelettatore(@ModelAttribute Toelettatore toelettatore,
                                    RedirectAttributes redirectAttributes) {
        toelettatoreService.save(toelettatore);
        redirectAttributes.addFlashAttribute("successo", "Toelettatore creato con successo.");
        return "redirect:/admin/toelettatori";
    }

    @GetMapping("/toelettatori/{id}/modifica")
    public String modificaToelettatore(@PathVariable Long id, Model model) {
        model.addAttribute("toelettatore", toelettatoreService.findById(id));
        return "admin/toelettatore/form";
    }

    @PostMapping("/toelettatori/{id}/modifica")
    public String aggiornaToelettatore(@PathVariable Long id,
                                       @ModelAttribute Toelettatore toelettatore,
                                       RedirectAttributes redirectAttributes) {
        toelettatoreService.update(id, toelettatore);
        redirectAttributes.addFlashAttribute("successo", "Toelettatore aggiornato.");
        return "redirect:/admin/toelettatori";
    }

    @PostMapping("/toelettatori/{id}/elimina")
    public String eliminaToelettatore(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        toelettatoreService.delete(id);
        redirectAttributes.addFlashAttribute("successo", "Toelettatore eliminato.");
        return "redirect:/admin/toelettatori";
    }

    @GetMapping("/fasce-orarie")
    public String elencoFasce(Model model) {
        model.addAttribute("fasce", fasciaOrariaService.findAll());
        return "admin/fascia/elenco";
    }

    @GetMapping("/fasce-orarie/nuova")
    public String nuovaFascia(Model model) {
        model.addAttribute("fascia", new FasciaOraria());
        model.addAttribute("toelettatori", toelettatoreService.findAttivi());
        return "admin/fascia/form";
    }

    @PostMapping("/fasce-orarie/nuova")
    public String salvaFascia(@ModelAttribute FasciaOraria fascia,
                              @RequestParam Long toelettatoreId,
                              RedirectAttributes redirectAttributes) {
        fasciaOrariaService.save(fascia, toelettatoreId);
        redirectAttributes.addFlashAttribute("successo", "Fascia oraria creata.");
        return "redirect:/admin/fasce-orarie";
    }
    
    @GetMapping("/fasce-orarie/genera")
    public String generaFasceForm(Model model) {
        model.addAttribute("toelettatori", toelettatoreService.findAttivi());
        return "admin/fascia/genera";
    }

    @PostMapping("/fasce-orarie/genera")
    public String generaFasce(@RequestParam Long toelettatoreId,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInizio,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFine,
                              @RequestParam List<DayOfWeek> giorni,
                              @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime oraInizio,
                              @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime oraFine,
                              @RequestParam Integer durataMinuti,
                              RedirectAttributes redirectAttributes) {
        try {
            int create = fasciaOrariaService.generaFasce(
                toelettatoreId,
                dataInizio,
                dataFine,
                giorni,
                oraInizio,
                oraFine,
                durataMinuti
            );

            redirectAttributes.addFlashAttribute("successo", "Generate " + create + " fasce orarie.");
            return "redirect:/admin/fasce-orarie";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errore", e.getMessage());
            return "redirect:/admin/fasce-orarie/genera";
        }
    }

    @GetMapping("/fasce-orarie/{id}/modifica")
    public String modificaFascia(@PathVariable Long id, Model model) {
        model.addAttribute("fascia", fasciaOrariaService.findById(id));
        model.addAttribute("toelettatori", toelettatoreService.findAttivi());
        return "admin/fascia/form";
    }

    @PostMapping("/fasce-orarie/{id}/modifica")
    public String aggiornaFascia(@PathVariable Long id,
                                 @ModelAttribute FasciaOraria fascia,
                                 @RequestParam Long toelettatoreId,
                                 RedirectAttributes redirectAttributes) {
        fasciaOrariaService.update(id, fascia, toelettatoreId);
        redirectAttributes.addFlashAttribute("successo", "Fascia oraria aggiornata.");
        return "redirect:/admin/fasce-orarie";
    }

    @PostMapping("/fasce-orarie/{id}/elimina")
    public String eliminaFascia(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        fasciaOrariaService.delete(id);
        redirectAttributes.addFlashAttribute("successo", "Fascia oraria eliminata.");
        return "redirect:/admin/fasce-orarie";
    }

    @GetMapping("/prenotazioni")
    public String elencoPrenotazioni(Model model) {
        model.addAttribute("prenotazioniConfermate", prenotazioneService.findConfermate());
        model.addAttribute("prenotazioniAnnullate", prenotazioneService.findAnnullate());
        return "admin/prenotazione/elenco";
    }

    @PostMapping("/prenotazioni/{id}/annulla")
    public String annullaPrenotazione(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        prenotazioneService.annullaDaAdmin(id);
        redirectAttributes.addFlashAttribute("successo", "Prenotazione annullata.");
        return "redirect:/admin/prenotazioni";
    }
}