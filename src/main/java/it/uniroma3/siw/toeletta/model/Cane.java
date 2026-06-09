package it.uniroma3.siw.toeletta.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
@Table(name = "Cani")
public class Cane {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "nome obbligatorio")
	@Column(nullable = false)
	private String nome;
	
	@NotBlank(message = "Inserire razza")
	@Column(nullable = false)
	private String razza;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private TagliaCane taglia;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private TipoPelo tipoPelo;
	
	@Column(nullable = true)
	private LocalDate dataNascita;
	
	@Column(nullable = true)
	private String note;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "proprietario_id", nullable = false)
	private Utente proprietario;
	
	@OneToMany(mappedBy = "cane", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Prenotazione> prenotazioni = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getRazza() {
		return razza;
	}

	public void setRazza(String razza) {
		this.razza = razza;
	}

	public TagliaCane getTaglia() {
		return taglia;
	}

	public void setTaglia(TagliaCane taglia) {
		this.taglia = taglia;
	}

	public TipoPelo getTipoPelo() {
		return tipoPelo;
	}

	public void setTipoPelo(TipoPelo tipoPelo) {
		this.tipoPelo = tipoPelo;
	}

	public LocalDate getDataNascita() {
		return dataNascita;
	}

	public void setDataNascita(LocalDate dataNascita) {
		this.dataNascita = dataNascita;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Utente getProprietario() {
		return proprietario;
	}

	public void setProprietario(Utente proprietario) {
		this.proprietario = proprietario;
	}

	public List<Prenotazione> getPrenotazioni() {
		return prenotazioni;
	}

	public void setPrenotazioni(List<Prenotazione> prenotazioni) {
		this.prenotazioni = prenotazioni;
	}
	
	
}
