package it.uniroma3.siw.toeletta.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.toeletta.model.Cane;
import it.uniroma3.siw.toeletta.model.TagliaCane;
import it.uniroma3.siw.toeletta.model.TipoPelo;

public interface CaneRepository extends JpaRepository<Cane, Long> {

    List<Cane> findByProprietarioId(Long proprietarioId);

    List<Cane> findByNomeContainingIgnoreCase(String nome);

    List<Cane> findByRazzaContainingIgnoreCase(String razza);

    List<Cane> findByTaglia(TagliaCane taglia);

    List<Cane> findByTipoPelo(TipoPelo tipoPelo);

    @Query("""
        SELECT c FROM Cane c
        JOIN FETCH c.proprietario
        WHERE c.id = :id
        """)
    Optional<Cane> findByIdWithProprietario(@Param("id") Long id);
}