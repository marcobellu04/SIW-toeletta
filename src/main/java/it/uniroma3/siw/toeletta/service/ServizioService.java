package it.uniroma3.siw.toeletta.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.toeletta.model.Servizio;
import it.uniroma3.siw.toeletta.repository.ServizioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ServizioService {

    @Autowired
    private ServizioRepository servizioRepository;

    @Transactional(readOnly = true)
    public List<Servizio> findAll() {
        return servizioRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public List<Servizio> findAttivi() {
        return servizioRepository.findByAttivoTrueOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public Servizio findById(Long id) {
        return servizioRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Servizio non trovato: " + id));
    }

    @Transactional
    public Servizio save(Servizio servizio) {
        if (servizio.getAttivo() == null) {
            servizio.setAttivo(true);
        }
        return servizioRepository.save(servizio);
    }

    @Transactional
    public Servizio update(Long id, Servizio dati) {
        Servizio servizio = findById(id);

        servizio.setNome(dati.getNome());
        servizio.setDescrizione(dati.getDescrizione());
        servizio.setDurataMinuti(dati.getDurataMinuti());
        servizio.setPrezzoBase(dati.getPrezzoBase());
        servizio.setAttivo(dati.getAttivo());

        return servizioRepository.save(servizio);
    }

    @Transactional
    public void delete(Long id) {
        servizioRepository.deleteById(id);
    }
    
}