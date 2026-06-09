package it.uniroma3.siw.toeletta.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.toeletta.model.Cane;
import it.uniroma3.siw.toeletta.model.Utente;
import it.uniroma3.siw.toeletta.repository.CaneRepository;
import it.uniroma3.siw.toeletta.repository.UtenteRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CaneService {

    @Autowired
    private CaneRepository caneRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Transactional(readOnly = true)
    public Cane findById(Long id) {
        return caneRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Cane non trovato: " + id));
    }

    @Transactional(readOnly = true)
    public List<Cane> findByProprietario(Long proprietarioId) {
        return caneRepository.findByProprietarioIdOrderByNomeAsc(proprietarioId);
    }

    @Transactional
    public Cane registraCane(Cane cane, Long proprietarioId) {
        Utente proprietario = utenteRepository.findById(proprietarioId)
            .orElseThrow(() -> new EntityNotFoundException("Proprietario non trovato: " + proprietarioId));

        cane.setProprietario(proprietario);
        return caneRepository.save(cane);
    }

    @Transactional
    public Cane update(Long id, Cane dati) {
        Cane cane = findById(id);

        cane.setNome(dati.getNome());
        cane.setRazza(dati.getRazza());
        cane.setTaglia(dati.getTaglia());
        cane.setTipoPelo(dati.getTipoPelo());
        cane.setDataNascita(dati.getDataNascita());
        cane.setNote(dati.getNote());

        return caneRepository.save(cane);
    }

    @Transactional
    public void delete(Long id) {
        caneRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Cane findByIdWithProprietario(Long id) {
        return caneRepository.findByIdWithProprietario(id)
            .orElseThrow(() -> new EntityNotFoundException("Cane non trovato: " + id));
    }

    @Transactional(readOnly = true)
    public boolean appartieneAUsername(Long caneId, String username) {
        Cane cane = findByIdWithProprietario(caneId);
        return cane.getProprietario().getUsername().equals(username);
    }
}