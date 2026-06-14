package it.uniroma3.siw.toeletta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.toeletta.model.Servizio;

public interface ServizioRepository extends JpaRepository<Servizio, Long> {

    List<Servizio> findByAttivoTrueOrderByNomeAsc();

    List<Servizio> findByNomeContainingIgnoreCase(String nome);

    List<Servizio> findByDurataMinutiLessThanEqual(Integer durataMinuti);

    List<Servizio> findAllByOrderByNomeAsc();

    boolean existsByNomeIgnoreCase(String nome);
}