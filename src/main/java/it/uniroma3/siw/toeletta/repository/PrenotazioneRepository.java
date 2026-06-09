package it.uniroma3.siw.toeletta.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.toeletta.model.Prenotazione;
import it.uniroma3.siw.toeletta.model.StatoPrenotazione;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {

    @Query("""
        SELECT p FROM Prenotazione p
        JOIN FETCH p.cane
        JOIN FETCH p.utente
        JOIN FETCH p.toelettatore
        JOIN FETCH p.servizio
        JOIN FETCH p.fasciaOraria f
        WHERE p.id = :id
        """)
    Optional<Prenotazione> findByIdWithDetails(@Param("id") Long id);
    
    @Query("""
    	    SELECT p FROM Prenotazione p
    	    JOIN FETCH p.cane
    	    JOIN FETCH p.utente
    	    JOIN FETCH p.toelettatore
    	    JOIN FETCH p.servizio
    	    JOIN FETCH p.fasciaOraria f
    	    WHERE p.stato = :stato
    	    ORDER BY f.data ASC, f.oraInizio ASC
    	    """)
    	List<Prenotazione> findAllByStatoWithDetails(@Param("stato") StatoPrenotazione stato);

    @Query("""
        SELECT p FROM Prenotazione p
        JOIN FETCH p.cane
        JOIN FETCH p.utente
        JOIN FETCH p.toelettatore
        JOIN FETCH p.servizio
        JOIN FETCH p.fasciaOraria f
        WHERE p.utente.id = :utenteId
          AND p.stato = :stato
        ORDER BY f.data ASC, f.oraInizio ASC
        """)
    List<Prenotazione> findByUtenteIdAndStatoWithDetails(@Param("utenteId") Long utenteId,
                                                          @Param("stato") StatoPrenotazione stato);

    @Query("""
        SELECT p FROM Prenotazione p
        JOIN FETCH p.cane
        JOIN FETCH p.utente
        JOIN FETCH p.toelettatore
        JOIN FETCH p.servizio
        JOIN FETCH p.fasciaOraria f
        WHERE p.cane.id = :caneId
          AND p.stato = :stato
        ORDER BY f.data ASC, f.oraInizio ASC
        """)
    List<Prenotazione> findByCaneIdAndStatoWithDetails(@Param("caneId") Long caneId,
                                                        @Param("stato") StatoPrenotazione stato);

    @Query("""
        SELECT p FROM Prenotazione p
        JOIN FETCH p.cane
        JOIN FETCH p.utente
        JOIN FETCH p.toelettatore
        JOIN FETCH p.servizio
        JOIN FETCH p.fasciaOraria f
        WHERE p.toelettatore.id = :toelettatoreId
          AND p.stato = :stato
        ORDER BY f.data ASC, f.oraInizio ASC
        """)
    List<Prenotazione> findByToelettatoreIdAndStatoWithDetails(@Param("toelettatoreId") Long toelettatoreId,
                                                                @Param("stato") StatoPrenotazione stato);

    boolean existsByFasciaOrariaIdAndStato(Long fasciaOrariaId, StatoPrenotazione stato);
}