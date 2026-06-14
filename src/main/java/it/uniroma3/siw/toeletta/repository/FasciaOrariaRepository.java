package it.uniroma3.siw.toeletta.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
        WHERE f.data >= :dataMinima
        ORDER BY f.data ASC, f.oraInizio ASC
        """)
    List<FasciaOraria> findFutureWithToelettatoreOrderByDataAscOraInizioAsc(@Param("dataMinima") LocalDate dataMinima);

    @Query("""
        SELECT f FROM FasciaOraria f
        LEFT JOIN FETCH f.toelettatore
        WHERE f.data = :data
        ORDER BY f.data ASC, f.oraInizio ASC
        """)
    List<FasciaOraria> findByDataWithToelettatore(@Param("data") LocalDate data);

    @Query("""
        SELECT f FROM FasciaOraria f
        JOIN FETCH f.toelettatore
        WHERE f.toelettatore.id = :toelettatoreId
        AND f.data >= :dataMinima
        ORDER BY f.data ASC, f.oraInizio ASC
        """)
    List<FasciaOraria> findFutureByToelettatoreWithToelettatore(@Param("toelettatoreId") Long toelettatoreId,
                                                                 @Param("dataMinima") LocalDate dataMinima);

    @Query("""
        SELECT f FROM FasciaOraria f
        JOIN FETCH f.toelettatore
        WHERE f.data = :data
        AND f.toelettatore.id = :toelettatoreId
        ORDER BY f.data ASC, f.oraInizio ASC
        """)
    List<FasciaOraria> findByDataAndToelettatoreWithToelettatore(@Param("data") LocalDate data,
                                                                  @Param("toelettatoreId") Long toelettatoreId);

    @Query("""
        SELECT f FROM FasciaOraria f
        JOIN FETCH f.toelettatore
        WHERE f.disponibile = true
        AND f.toelettatore IS NOT NULL
        AND f.data >= :dataMinima
        ORDER BY f.data ASC, f.oraInizio ASC
        """)
    List<FasciaOraria> findFutureDisponibiliAssegnateOrderByDataAscOraInizioAsc(@Param("dataMinima") LocalDate dataMinima);

    boolean existsByDataAndOraInizio(LocalDate data, LocalTime oraInizio);

    boolean existsByToelettatoreIdAndDataAndOraInizio(Long toelettatoreId, LocalDate data, LocalTime oraInizio);
}