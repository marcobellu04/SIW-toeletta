package it.uniroma3.siw.toeletta.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name= "Utenti")
public class Utente {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "L'username è obbligatorio")
	@Column(unique=true, nullable = false)
	private String username;
	
	@NotBlank(message = "La password è obbligatoria")
	@Column(nullable = false)
	private String password;
	
	@NotBlank(message = "Inserire il nome")
	@Column(nullable = false)
	private String nome;
	
	@NotBlank(message = "Inserire il cognome")
	@Column(nullable = false)
	private String cognome;

	@Column(unique=true, nullable = true)
	private String email;

	@Column(unique=true, nullable = true)
	private String telefono;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)   
	private RuoloUtente ruolo;
	
	@OneToMany(mappedBy = "proprietario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Cane> cani = new ArrayList<>();
	
	@OneToMany(mappedBy = "utente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Prenotazione> prenotazioni = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public RuoloUtente getRuolo() {
		return ruolo;
	}

	public void setRuolo(RuoloUtente ruolo) {
		this.ruolo = ruolo;
	}

	public List<Cane> getCani() {
		return cani;
	}

	public void setCani(List<Cane> cani) {
		this.cani = cani;
	}

	public List<Prenotazione> getPrenotazioni() {
		return prenotazioni;
	}

	public void setPrenotazioni(List<Prenotazione> prenotazioni) {
		this.prenotazioni = prenotazioni;
	}
	
	
}
