package it.uniroma3.siw.toeletta.model;

import java.math.BigDecimal;
import java.time.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name= "prenotazioni")
public class Prenotazione {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Column(nullable=false)
	private LocalDateTime dataCreazione = LocalDateTime.now();
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private StatoPrenotazione stato = StatoPrenotazione.CONFERMATA;
	
	private String noteCliente;
	
	@NotNull
	@Column(nullable=false)
	private BigDecimal prezzoFinale;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "utente_id", nullable = false)
	private Utente utente;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cane_id", nullable = false)
	private Cane cane;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "toelettatore_id", nullable = false)
	private Toelettatore toelettatore;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "servizio_id", nullable = false)
	private Servizio servizio;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fascia_oraria_id", nullable = false)
	private FasciaOraria fasciaOraria;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(LocalDateTime dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public StatoPrenotazione getStato() {
		return stato;
	}

	public void setStato(StatoPrenotazione stato) {
		this.stato = stato;
	}

	public String getNoteCliente() {
		return noteCliente;
	}

	public void setNoteCliente(String noteCliente) {
		this.noteCliente = noteCliente;
	}

	public BigDecimal getPrezzoFinale() {
		return prezzoFinale;
	}

	public void setPrezzoFinale(BigDecimal prezzoFinale) {
		this.prezzoFinale = prezzoFinale;
	}

	public Utente getUtente() {
		return utente;
	}

	public void setUtente(Utente utente) {
		this.utente = utente;
	}

	public Cane getCane() {
		return cane;
	}

	public void setCane(Cane cane) {
		this.cane = cane;
	}

	public Toelettatore getToelettatore() {
		return toelettatore;
	}

	public void setToelettatore(Toelettatore toelettatore) {
		this.toelettatore = toelettatore;
	}

	public Servizio getServizio() {
		return servizio;
	}

	public void setServizio(Servizio servizio) {
		this.servizio = servizio;
	}

	public FasciaOraria getFasciaOraria() {
		return fasciaOraria;
	}

	public void setFasciaOraria(FasciaOraria fasciaOraria) {
		this.fasciaOraria = fasciaOraria;
	}
	
	
}
