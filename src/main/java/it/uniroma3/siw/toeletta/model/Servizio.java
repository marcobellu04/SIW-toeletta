package it.uniroma3.siw.toeletta.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "servizi")
public class Servizio {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "inserire il servizio")
	@Column(unique=false, nullable=false)
	private String nome;
	
	@NotBlank(message = "inserire descrizione")
	@Column(unique=false, nullable=false)
	private String descrizione;
	
	@NotNull(message = "inserire la durata")
	@Column(unique=false, nullable=false)
	private Integer durataMinuti;
	
	@NotNull(message = "inserire il prezzo")
	@Column(unique=false, nullable=false)
	private BigDecimal prezzoBase;
	
	@Column(nullable=false)
	private Boolean attivo;
	
	@OneToMany(mappedBy="servizio", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
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

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Integer getDurataMinuti() {
		return durataMinuti;
	}

	public void setDurataMinuti(Integer durataMinuti) {
		this.durataMinuti = durataMinuti;
	}

	public BigDecimal getPrezzoBase() {
		return prezzoBase;
	}

	public void setPrezzoBase(BigDecimal prezzoBase) {
		this.prezzoBase = prezzoBase;
	}

	public Boolean getAttivo() {
		return attivo;
	}

	public void setAttivo(Boolean attivo) {
		this.attivo = attivo;
	}

	public List<Prenotazione> getPrenotazioni() {
		return prenotazioni;
	}

	public void setPrenotazioni(List<Prenotazione> prenotazioni) {
		this.prenotazioni = prenotazioni;
	}
	
	
}
