package it.uniroma3.siw.toeletta.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "toelettatore")
public class Toelettatore {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "inserire il nome")
	@Column(unique=false, nullable=false)
	private String nome;
	
	@NotBlank(message = "inserire il cognome")
	@Column(unique=false, nullable=false)
	private String cognome;
	
	@NotBlank(message = "inserire il numero di telefono")
	@Column(unique=true, nullable=false)
	private String telefono;
	
	private String specializzazione;
	
	@Column(nullable=false)
	private Boolean attivo;
	
	@OneToMany(mappedBy = "toelettatore", cascade=CascadeType.ALL, fetch= FetchType.LAZY)
	private List<FasciaOraria> fasceOrarie = new ArrayList<>();
	
	@OneToMany(mappedBy = "toelettatore", cascade=CascadeType.ALL, fetch= FetchType.LAZY)
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

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getSpecializzazione() {
		return specializzazione;
	}

	public void setSpecializzazione(String specializzazione) {
		this.specializzazione = specializzazione;
	}

	public Boolean getAttivo() {
		return attivo;
	}

	public void setAttivo(Boolean attivo) {
		this.attivo = attivo;
	}

	public List<FasciaOraria> getFasceOrarie() {
		return fasceOrarie;
	}

	public void setFasceOrarie(List<FasciaOraria> fasceOrarie) {
		this.fasceOrarie = fasceOrarie;
	}

	public List<Prenotazione> getPrenotazioni() {
		return prenotazioni;
	}

	public void setPrenotazioni(List<Prenotazione> prenotazioni) {
		this.prenotazioni = prenotazioni;
	}
	
	
}
