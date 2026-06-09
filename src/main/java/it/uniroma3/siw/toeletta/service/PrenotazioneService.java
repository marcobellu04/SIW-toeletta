package it.uniroma3.siw.toeletta.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.toeletta.model.Cane;
import it.uniroma3.siw.toeletta.model.FasciaOraria;
import it.uniroma3.siw.toeletta.model.Prenotazione;
import it.uniroma3.siw.toeletta.model.Servizio;
import it.uniroma3.siw.toeletta.model.StatoPrenotazione;
import it.uniroma3.siw.toeletta.model.Toelettatore;
import it.uniroma3.siw.toeletta.model.Utente;
import it.uniroma3.siw.toeletta.repository.CaneRepository;
import it.uniroma3.siw.toeletta.repository.FasciaOrariaRepository;
import it.uniroma3.siw.toeletta.repository.PrenotazioneRepository;
import it.uniroma3.siw.toeletta.repository.ServizioRepository;
import it.uniroma3.siw.toeletta.repository.ToelettatoreRepository;
import it.uniroma3.siw.toeletta.repository.UtenteRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PrenotazioneService {

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private CaneRepository caneRepository;

    @Autowired
    private ServizioRepository servizioRepository;

    @Autowired
    private ToelettatoreRepository toelettatoreRepository;

    @Autowired
    private FasciaOrariaRepository fasciaOrariaRepository;

    @Transactional(readOnly = true)
    public Prenotazione findById(Long id) {
        return prenotazioneRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new EntityNotFoundException("Prenotazione non trovata: " + id));
    }

    @Transactional(readOnly = true)
    public List<Prenotazione> findConfermateByUtente(Long utenteId) {
        return prenotazioneRepository.findByUtenteIdAndStatoWithDetails(
            utenteId,
            StatoPrenotazione.CONFERMATA
        );
    }

    @Transactional(readOnly = true)
    public List<Prenotazione> findAnnullateByUtente(Long utenteId) {
        return prenotazioneRepository.findByUtenteIdAndStatoWithDetails(
            utenteId,
            StatoPrenotazione.ANNULLATA
        );
    }

    @Transactional(readOnly = true)
    public List<Prenotazione> findConfermateByCane(Long caneId) {
        return prenotazioneRepository.findByCaneIdAndStatoWithDetails(
            caneId,
            StatoPrenotazione.CONFERMATA
        );
    }

    @Transactional(readOnly = true)
    public List<Prenotazione> findConfermateByToelettatore(Long toelettatoreId) {
        return prenotazioneRepository.findByToelettatoreIdAndStatoWithDetails(
            toelettatoreId,
            StatoPrenotazione.CONFERMATA
        );
    }
    
    @Transactional(readOnly = true)
    public List<Prenotazione> findConfermate() {
        return prenotazioneRepository.findAllByStatoWithDetails(StatoPrenotazione.CONFERMATA);
    }

    @Transactional(readOnly = true)
    public List<Prenotazione> findAnnullate() {
        return prenotazioneRepository.findAllByStatoWithDetails(StatoPrenotazione.ANNULLATA);
    }

    @Transactional
    public Prenotazione prenota(Long utenteId,
                                Long caneId,
                                Long servizioId,
                                Long toelettatoreId,
                                Long fasciaOrariaId,
                                String noteCliente) {

        Utente utente = utenteRepository.findById(utenteId)
            .orElseThrow(() -> new EntityNotFoundException("Utente non trovato: " + utenteId));

        Cane cane = caneRepository.findById(caneId)
            .orElseThrow(() -> new EntityNotFoundException("Cane non trovato: " + caneId));

        if (!cane.getProprietario().getId().equals(utente.getId())) {
            throw new IllegalArgumentException("Il cane selezionato non appartiene all'utente.");
        }

        Servizio servizio = servizioRepository.findById(servizioId)
            .orElseThrow(() -> new EntityNotFoundException("Servizio non trovato: " + servizioId));

        Toelettatore toelettatore = toelettatoreRepository.findById(toelettatoreId)
            .orElseThrow(() -> new EntityNotFoundException("Toelettatore non trovato: " + toelettatoreId));

        FasciaOraria fasciaOraria = fasciaOrariaRepository.findById(fasciaOrariaId)
            .orElseThrow(() -> new EntityNotFoundException("Fascia oraria non trovata: " + fasciaOrariaId));

        if (!Boolean.TRUE.equals(fasciaOraria.getDisponibile())) {
            throw new IllegalStateException("La fascia oraria selezionata non e' disponibile.");
        }
 
        if (!fasciaOraria.getToelettatore().getId().equals(toelettatore.getId())) {
            throw new IllegalArgumentException("La fascia oraria non appartiene al toelettatore selezionato.");
        }

        boolean giaPrenotata = prenotazioneRepository.existsByFasciaOrariaIdAndStato(
            fasciaOrariaId,
            StatoPrenotazione.CONFERMATA
        );

        if (giaPrenotata) {
            throw new IllegalStateException("Esiste gia una prenotazione confermata per questa fascia oraria.");
        }

        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setUtente(utente);
        prenotazione.setCane(cane);
        prenotazione.setServizio(servizio);
        prenotazione.setToelettatore(toelettatore);
        prenotazione.setFasciaOraria(fasciaOraria);
        prenotazione.setNoteCliente(noteCliente);
        prenotazione.setPrezzoFinale(servizio.getPrezzoBase());
        prenotazione.setStato(StatoPrenotazione.CONFERMATA);

        fasciaOraria.setDisponibile(false);

        return prenotazioneRepository.save(prenotazione);
    }

    @Transactional
    public void annulla(Long prenotazioneId, Long utenteId) {
        Prenotazione prenotazione = findById(prenotazioneId);

        if (!prenotazione.getUtente().getId().equals(utenteId)) {
            throw new IllegalArgumentException("Non puoi annullare una prenotazione di un altro utente.");
        }

        prenotazione.setStato(StatoPrenotazione.ANNULLATA);
        prenotazione.getFasciaOraria().setDisponibile(true);

        prenotazioneRepository.save(prenotazione);
    }

    @Transactional
    public void annullaDaAdmin(Long prenotazioneId) {
        Prenotazione prenotazione = findById(prenotazioneId);

        prenotazione.setStato(StatoPrenotazione.ANNULLATA);
        prenotazione.getFasciaOraria().setDisponibile(true);

        prenotazioneRepository.save(prenotazione);
    }
}