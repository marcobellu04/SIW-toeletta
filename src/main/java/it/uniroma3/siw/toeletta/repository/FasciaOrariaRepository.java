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
    
    @Query("""
    	    SELECT f FROM FasciaOraria f
    	    JOIN FETCH f.toelettatore
    	    ORDER BY f.data ASC, f.oraInizio ASC
    	    """)
    	List<FasciaOraria> findAllWithToelettatoreOrderByDataAscOraInizioAsc();

    boolean existsByToelettatoreIdAndDataAndOraInizio(Long toelettatoreId, LocalDate data, LocalTime oraInizio);
}
