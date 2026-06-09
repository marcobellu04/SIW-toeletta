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
    public int generaFasce(Long toelettatoreId,
                           LocalDate dataInizio,
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

        Toelettatore toelettatore = toelettatoreRepository.findById(toelettatoreId)
            .orElseThrow(() -> new EntityNotFoundException("Toelettatore non trovato: " + toelettatoreId));

        int create = 0;
        LocalDate giorno = dataInizio;

        while (!giorno.isAfter(dataFine)) {
            if (giorni.contains(giorno.getDayOfWeek())) {
                LocalTime inizioSlot = oraInizio;

                while (!inizioSlot.plusMinutes(durataMinuti).isAfter(oraFine)) {
                    boolean giaEsiste = fasciaOrariaRepository.existsByToelettatoreIdAndDataAndOraInizio(
                        toelettatoreId,
                        giorno,
                        inizioSlot
                    );

                    if (!giaEsiste) {
                        FasciaOraria fascia = new FasciaOraria();
                        fascia.setToelettatore(toelettatore);
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

    @Transactional(readOnly = true)
    public List<FasciaOraria> findDisponibili() {
        return fasciaOrariaRepository.findByDisponibileTrueOrderByDataAscOraInizioAsc();
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
        Toelettatore toelettatore = toelettatoreRepository.findById(toelettatoreId)
            .orElseThrow(() -> new EntityNotFoundException("Toelettatore non trovato: " + toelettatoreId));

        boolean giaEsiste = fasciaOrariaRepository.existsByToelettatoreIdAndDataAndOraInizio(
            toelettatoreId,
            fasciaOraria.getData(),
            fasciaOraria.getOraInizio()
        );

        if (giaEsiste) {
            throw new IllegalArgumentException("Esiste gia una fascia oraria per questo toelettatore nella stessa data e ora.");
        }

        fasciaOraria.setToelettatore(toelettatore);
        fasciaOraria.setDisponibile(true);

        return fasciaOrariaRepository.save(fasciaOraria);
    }

    @Transactional
    public FasciaOraria update(Long id, FasciaOraria dati, Long toelettatoreId) {
        FasciaOraria fascia = findById(id);

        Toelettatore toelettatore = toelettatoreRepository.findById(toelettatoreId)
            .orElseThrow(() -> new EntityNotFoundException("Toelettatore non trovato: " + toelettatoreId));

        fascia.setData(dati.getData());
        fascia.setOraInizio(dati.getOraInizio());
        fascia.setOraFine(dati.getOraFine());
        fascia.setDisponibile(dati.getDisponibile());
        fascia.setToelettatore(toelettatore);

        return fasciaOrariaRepository.save(fascia);
    }

    @Transactional
    public void delete(Long id) {
        fasciaOrariaRepository.deleteById(id);
    }
}