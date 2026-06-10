package it.uniroma3.siw.toeletta.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.toeletta.model.FasciaOraria;
import it.uniroma3.siw.toeletta.model.Toelettatore;
import it.uniroma3.siw.toeletta.repository.FasciaOrariaRepository;
import it.uniroma3.siw.toeletta.repository.ToelettatoreRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class FasciaOrariaService {

    @Autowired
    private FasciaOrariaRepository fasciaOrariaRepository;

    @Autowired
    private ToelettatoreRepository toelettatoreRepository;

    @Transactional(readOnly = true)
    public FasciaOraria findById(Long id) {
        return fasciaOrariaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Fascia oraria non trovata: " + id));
    }

    @Transactional(readOnly = true)
    public List<FasciaOraria> findAll() {
        return fasciaOrariaRepository.findAllWithToelettatoreOrderByDataAscOraInizioAsc();
    }

    @Transactional
    public int generaFasce(LocalDate dataInizio,
                           LocalDate dataFine,
                           List<DayOfWeek> giorni,
                           LocalTime oraInizio,
                           LocalTime oraFine,
                           Integer durataMinuti) {
        if (dataFine.isBefore(dataInizio)) {
            throw new IllegalArgumentException("La data fine non puo essere precedente alla data inizio.");
        }

        if (!oraFine.isAfter(oraInizio)) {
            throw new IllegalArgumentException("L'ora fine deve essere successiva all'ora inizio.");
        }

        if (durataMinuti == null || durataMinuti <= 0) {
            throw new IllegalArgumentException("La durata dello slot deve essere positiva.");
        }

        int create = 0;
        LocalDate giorno = dataInizio;

        while (!giorno.isAfter(dataFine)) {
            if (giorni.contains(giorno.getDayOfWeek())) {
                LocalTime inizioSlot = oraInizio;

                while (!inizioSlot.plusMinutes(durataMinuti).isAfter(oraFine)) {
                    boolean giaEsiste = fasciaOrariaRepository.existsByDataAndOraInizio(giorno, inizioSlot);

                    if (!giaEsiste) {
                        FasciaOraria fascia = new FasciaOraria();
                        fascia.setData(giorno);
                        fascia.setOraInizio(inizioSlot);
                        fascia.setOraFine(inizioSlot.plusMinutes(durataMinuti));
                        fascia.setDisponibile(true);

                        fasciaOrariaRepository.save(fascia);
                        create++;
                    }

                    inizioSlot = inizioSlot.plusMinutes(durataMinuti);
                }
            }

            giorno = giorno.plusDays(1);
        }

        return create;
    }


    @Transactional
    public int assegnaTurno(Long toelettatoreId,
                            LocalDate dataInizio,
                            LocalDate dataFine,
                            List<DayOfWeek> giorni,
                            LocalTime oraInizio,
                            LocalTime oraFine) {
        if (dataFine.isBefore(dataInizio)) {
            throw new IllegalArgumentException("La data fine non puo essere precedente alla data inizio.");
        }

        if (!oraFine.isAfter(oraInizio)) {
            throw new IllegalArgumentException("L'ora fine deve essere successiva all'ora inizio.");
        }

        Toelettatore toelettatore = toelettatoreRepository.findById(toelettatoreId)
            .orElseThrow(() -> new EntityNotFoundException("Toelettatore non trovato: " + toelettatoreId));

        int aggiornate = 0;
        List<FasciaOraria> fasce = fasciaOrariaRepository
            .findByDataBetweenOrderByDataAscOraInizioAsc(dataInizio, dataFine);

        for (FasciaOraria fascia : fasce) {
            boolean giornoIncluso = giorni.contains(fascia.getData().getDayOfWeek());
            boolean dentroTurno = !fascia.getOraInizio().isBefore(oraInizio)
                && !fascia.getOraFine().isAfter(oraFine);
            boolean modificabile = Boolean.TRUE.equals(fascia.getDisponibile());

            if (giornoIncluso && dentroTurno && modificabile) {
                fascia.setToelettatore(toelettatore);
                aggiornate++;
            }
        }

        return aggiornate;
    }

    @Transactional(readOnly = true)
    public List<FasciaOraria> findDisponibili() {
        return fasciaOrariaRepository.findDisponibiliAssegnateOrderByDataAscOraInizioAsc();
    }

    @Transactional(readOnly = true)
    public List<FasciaOraria> findDisponibiliByToelettatore(Long toelettatoreId) {
        return fasciaOrariaRepository
            .findByToelettatoreIdAndDisponibileTrueOrderByDataAscOraInizioAsc(toelettatoreId);
    }

    @Transactional(readOnly = true)
    public List<FasciaOraria> findByToelettatoreAndData(Long toelettatoreId, LocalDate data) {
        return fasciaOrariaRepository.findByToelettatoreIdAndDataOrderByOraInizioAsc(toelettatoreId, data);
    }

    @Transactional
    public FasciaOraria save(FasciaOraria fasciaOraria, Long toelettatoreId) {
        boolean giaEsiste = fasciaOrariaRepository.existsByDataAndOraInizio(
            fasciaOraria.getData(),
            fasciaOraria.getOraInizio()
        );

        if (giaEsiste) {
            throw new IllegalArgumentException("Esiste gia una fascia oraria nella stessa data e ora.");
        }

        assegnaToelettatore(fasciaOraria, toelettatoreId);
        fasciaOraria.setDisponibile(true);

        return fasciaOrariaRepository.save(fasciaOraria);
    }

    @Transactional
    public FasciaOraria update(Long id, FasciaOraria dati, Long toelettatoreId) {
        FasciaOraria fascia = findById(id);

        fascia.setData(dati.getData());
        fascia.setOraInizio(dati.getOraInizio());
        fascia.setOraFine(dati.getOraFine());
        fascia.setDisponibile(dati.getDisponibile());
        assegnaToelettatore(fascia, toelettatoreId);

        return fasciaOrariaRepository.save(fascia);
    }

    @Transactional
    public void delete(Long id) {
        fasciaOrariaRepository.deleteById(id);
    }

    private void assegnaToelettatore(FasciaOraria fascia, Long toelettatoreId) {
        if (toelettatoreId == null) {
            fascia.setToelettatore(null);
            return;
        }

        Toelettatore toelettatore = toelettatoreRepository.findById(toelettatoreId)
            .orElseThrow(() -> new EntityNotFoundException("Toelettatore non trovato: " + toelettatoreId));

        fascia.setToelettatore(toelettatore);
    }
}