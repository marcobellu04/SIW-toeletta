package it.uniroma3.siw.toeletta.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.toeletta.model.Toelettatore;
import it.uniroma3.siw.toeletta.repository.ToelettatoreRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ToelettatoreService {

    @Autowired
    private ToelettatoreRepository toelettatoreRepository;

    @Transactional(readOnly = true)
    public List<Toelettatore> findAll() {
        return toelettatoreRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Toelettatore> findAttivi() {
        return toelettatoreRepository.findByAttivoTrueOrderByCognomeAscNomeAsc();
    }

    @Transactional(readOnly = true)
    public Toelettatore findById(Long id) {
        return toelettatoreRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Toelettatore non trovato: " + id));
    }

    @Transactional
    public Toelettatore save(Toelettatore toelettatore) {
        if (toelettatore.getAttivo() == null) {
            toelettatore.setAttivo(true);
        }
        return toelettatoreRepository.save(toelettatore);
    }

    @Transactional
    public Toelettatore update(Long id, Toelettatore dati) {
        Toelettatore toelettatore = findById(id);

        toelettatore.setNome(dati.getNome());
        toelettatore.setCognome(dati.getCognome());
        toelettatore.setTelefono(dati.getTelefono());
        toelettatore.setSpecializzazione(dati.getSpecializzazione());
        toelettatore.setAttivo(dati.getAttivo());

        return toelettatoreRepository.save(toelettatore);
    }

    @Transactional
    public void delete(Long id) {
        toelettatoreRepository.deleteById(id);
    }
}