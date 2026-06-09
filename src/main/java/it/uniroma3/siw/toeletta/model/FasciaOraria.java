package it.uniroma3.siw.toeletta.model;

import java.time.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name= "orari")
public class FasciaOraria {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Column(nullable=false)
	private LocalDate data;
	
	@NotNull
	@Column(nullable=false)
	private LocalTime oraInizio;
	
	@NotNull
	@Column(nullable=false)
	private LocalTime oraFine;
	
	@Column( nullable=false)
	private Boolean disponibile = true;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "toelettatore_id", nullable = false)
	private Toelettatore toelettatore;
	
	@OneToOne(mappedBy = "fasciaOraria", fetch = FetchType.LAZY)
	private Prenotazione prenotazione;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public LocalTime getOraInizio() {
		return oraInizio;
	}

	public void setOraInizio(LocalTime oraInizio) {
		this.oraInizio = oraInizio;
	}

	public LocalTime getOraFine() {
		return oraFine;
	}

	public void setOraFine(LocalTime oraFine) {
		this.oraFine = oraFine;
	}

	public Boolean getDisponibile() {
		return disponibile;
	}

	public void setDisponibile(Boolean disponibile) {
		this.disponibile = disponibile;
	}

	public Toelettatore getToelettatore() {
		return toelettatore;
	}

	public void setToelettatore(Toelettatore toelettatore) {
		this.toelettatore = toelettatore;
	}

	public Prenotazione getPrenotazione() {
		return prenotazione;
	}

	public void setPrenotazione(Prenotazione prenotazione) {
		this.prenotazione = prenotazione;
	}

	
}
