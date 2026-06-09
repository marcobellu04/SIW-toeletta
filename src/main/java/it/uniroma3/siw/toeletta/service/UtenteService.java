package it.uniroma3.siw.toeletta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.toeletta.model.RuoloUtente;
import it.uniroma3.siw.toeletta.model.Utente;
import it.uniroma3.siw.toeletta.repository.UtenteRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Utente findById(Long id) {
        return utenteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Utente non trovato: " + id));
    }

    @Transactional(readOnly = true)
    public Utente findByUsername(String username) {
        return utenteRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("Utente non trovato: " + username));
    }

    @Transactional
    public Utente registra(Utente utente) {
        if (utenteRepository.existsByUsername(utente.getUsername())) {
            throw new IllegalArgumentException("Username gia in uso");
        }

        if (utente.getEmail() != null && !utente.getEmail().isBlank()
                && utenteRepository.existsByEmail(utente.getEmail())) {
            throw new IllegalArgumentException("Email gia in uso");
        }

        if (utente.getTelefono() != null && !utente.getTelefono().isBlank()
                && utenteRepository.existsByTelefono(utente.getTelefono())) {
            throw new IllegalArgumentException("Telefono gia in uso");
        }

        utente.setPassword(passwordEncoder.encode(utente.getPassword()));
        utente.setRuolo(RuoloUtente.USER);
        return utenteRepository.save(utente);
    }
}
