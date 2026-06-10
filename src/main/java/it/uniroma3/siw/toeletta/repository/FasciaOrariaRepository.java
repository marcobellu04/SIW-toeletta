package it.uniroma3.siw.toeletta.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.uniroma3.siw.toeletta.model.FasciaOraria;

public interface FasciaOrariaRepository extends JpaRepository<FasciaOraria, Long> {

    List<FasciaOraria> findByDisponibileTrueOrderByDataAscOraInizioAsc();

    List<FasciaOraria> findByToelettatoreIdAndDisponibileTrueOrderByDataAscOraInizioAsc(Long toelettatoreId);

    List<FasciaOraria> findByDataAndDisponibileTrueOrderByOraInizioAsc(LocalDate data);

    List<FasciaOraria> findByToelettatoreIdAndDataOrderByOraInizioAsc(Long toelettatoreId, LocalDate data);

    List<FasciaOraria> findAllByOrderByDataAscOraInizioAsc();

    List<FasciaOraria> findByDataBetweenOrderByDataAscOraInizioAsc(LocalDate dataInizio, LocalDate dataFine);

    @Query("""
        SELECT f FROM FasciaOraria f
        LEFT JOIN FETCH f.toelettatore
        ORDER BY f.data ASC, f.oraInizio ASC
        """)
    List<FasciaOraria> findAllWithToelettatoreOrderByDataAscOraInizioAsc();

    @Query("""
        SELECT f FROM FasciaOraria f
        JOIN FETCH f.toelettatore
        WHERE f.disponibile = true
        AND f.toelettatore IS NOT NULL
        ORDER BY f.data ASC, f.oraInizio ASC
        """)
    List<FasciaOraria> findDisponibiliAssegnateOrderByDataAscOraInizioAsc();

    boolean existsByDataAndOraInizio(LocalDate data, LocalTime oraInizio);

    boolean existsByToelettatoreIdAndDataAndOraInizio(Long toelettatoreId, LocalDate data, LocalTime oraInizio);
}