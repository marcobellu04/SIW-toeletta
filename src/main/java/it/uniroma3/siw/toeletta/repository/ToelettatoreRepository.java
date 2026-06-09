package it.uniroma3.siw.toeletta.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.toeletta.model.Toelettatore;

public interface ToelettatoreRepository extends JpaRepository<Toelettatore, Long> {

    List<Toelettatore> findByAttivoTrueOrderByCognomeAscNomeAsc();

    List<Toelettatore> findByCognomeContainingIgnoreCase(String cognome);

    List<Toelettatore> findByNomeContainingIgnoreCase(String nome);

    List<Toelettatore> findBySpecializzazioneContainingIgnoreCase(String specializzazione);

    Optional<Toelettatore> findByTelefono(String telefono);

    boolean existsByTelefono(String telefono);
}