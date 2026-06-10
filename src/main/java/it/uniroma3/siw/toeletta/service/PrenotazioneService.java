package it.uniroma3.siw.toeletta.service;

import java.time.Duration;
import java.util.ArrayList;
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

        FasciaOraria fasciaOraria = fasciaOrariaRepository.findById(fasciaOrariaId)
            .orElseThrow(() -> new EntityNotFoundException("Fascia oraria non trovata: " + fasciaOrariaId));

        Toelettatore toelettatore = fasciaOraria.getToelettatore();
        if (toelettatore == null) {
            throw new IllegalStateException("La fascia oraria selezionata non ha un toelettatore assegnato.");
        }

        List<FasciaOraria> fasceDaOccupare = trovaFasceConsecutiveDisponibili(fasciaOraria, servizio.getDurataMinuti());

        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setUtente(utente);
        prenotazione.setCane(cane);
        prenotazione.setServizio(servizio);
        prenotazione.setToelettatore(toelettatore);
        prenotazione.setFasciaOraria(fasciaOraria);
        prenotazione.setNoteCliente(noteCliente);
        prenotazione.setPrezzoFinale(servizio.getPrezzoBase());
        prenotazione.setStato(StatoPrenotazione.CONFERMATA);

        for (FasciaOraria fascia : fasceDaOccupare) {
            fascia.setDisponibile(false);
        }

        return prenotazioneRepository.save(prenotazione);
    }

    @Transactional
    public void annulla(Long prenotazioneId, Long utenteId) {
        Prenotazione prenotazione = findById(prenotazioneId);

        if (!prenotazione.getUtente().getId().equals(utenteId)) {
            throw new IllegalArgumentException("Non puoi annullare una prenotazione di un altro utente.");
        }

        annullaPrenotazione(prenotazione);
    }

    @Transactional
    public void annullaDaAdmin(Long prenotazioneId) {
        Prenotazione prenotazione = findById(prenotazioneId);
        annullaPrenotazione(prenotazione);
    }

    private void annullaPrenotazione(Prenotazione prenotazione) {
        prenotazione.setStato(StatoPrenotazione.ANNULLATA);

        List<FasciaOraria> fasceDaLiberare = trovaFasceConsecutiveDaLiberare(
            prenotazione.getFasciaOraria(),
            prenotazione.getServizio().getDurataMinuti()
        );

        for (FasciaOraria fascia : fasceDaLiberare) {
            fascia.setDisponibile(true);
        }

        prenotazioneRepository.save(prenotazione);
    }

    private List<FasciaOraria> trovaFasceConsecutiveDisponibili(FasciaOraria fasciaIniziale, Integer durataMinuti) {
        List<FasciaOraria> fasce = fasciaOrariaRepository.findByToelettatoreIdAndDataOrderByOraInizioAsc(
            fasciaIniziale.getToelettatore().getId(),
            fasciaIniziale.getData()
        );

        List<FasciaOraria> selezionate = new ArrayList<>();
        int minutiCoperti = 0;
        boolean iniziata = false;

        for (FasciaOraria fascia : fasce) {
            if (!iniziata) {
                if (!fascia.getId().equals(fasciaIniziale.getId())) {
                    continue;
                }
                iniziata = true;
            }

            if (!Boolean.TRUE.equals(fascia.getDisponibile())) {
                throw new IllegalStateException("Non ci sono abbastanza slot consecutivi disponibili per il servizio scelto.");
            }

            if (fascia.getToelettatore() == null || !fascia.getToelettatore().getId().equals(fasciaIniziale.getToelettatore().getId())) {
                throw new IllegalStateException("Gli slot consecutivi non appartengono allo stesso toelettatore.");
            }

            if (!selezionate.isEmpty()) {
                FasciaOraria precedente = selezionate.get(selezionate.size() - 1);
                if (!precedente.getOraFine().equals(fascia.getOraInizio())) {
                    throw new IllegalStateException("Non ci sono abbastanza slot consecutivi disponibili per il servizio scelto.");
                }
            }

            selezionate.add(fascia);
            minutiCoperti += durataFascia(fascia);

            if (minutiCoperti >= durataMinuti) {
                return selezionate;
            }
        }

        throw new IllegalStateException("Non ci sono abbastanza slot consecutivi disponibili per il servizio scelto.");
    }

    private List<FasciaOraria> trovaFasceConsecutiveDaLiberare(FasciaOraria fasciaIniziale, Integer durataMinuti) {
        List<FasciaOraria> fasce = fasciaOrariaRepository.findByToelettatoreIdAndDataOrderByOraInizioAsc(
            fasciaIniziale.getToelettatore().getId(),
            fasciaIniziale.getData()
        );

        List<FasciaOraria> selezionate = new ArrayList<>();
        int minutiCoperti = 0;
        boolean iniziata = false;

        for (FasciaOraria fascia : fasce) {
            if (!iniziata) {
                if (!fascia.getId().equals(fasciaIniziale.getId())) {
                    continue;
                }
                iniziata = true;
            }

            if (!selezionate.isEmpty()) {
                FasciaOraria precedente = selezionate.get(selezionate.size() - 1);
                if (!precedente.getOraFine().equals(fascia.getOraInizio())) {
                    break;
                }
            }

            selezionate.add(fascia);
            minutiCoperti += durataFascia(fascia);

            if (minutiCoperti >= durataMinuti) {
                return selezionate;
            }
        }

        return selezionate;
    }

    private int durataFascia(FasciaOraria fascia) {
        return (int) Duration.between(fascia.getOraInizio(), fascia.getOraFine()).toMinutes();
    }
}