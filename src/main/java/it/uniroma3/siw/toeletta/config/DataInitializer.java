package it.uniroma3.siw.toeletta.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.uniroma3.siw.toeletta.model.Cane;
import it.uniroma3.siw.toeletta.model.FasciaOraria;
import it.uniroma3.siw.toeletta.model.RuoloUtente;
import it.uniroma3.siw.toeletta.model.Servizio;
import it.uniroma3.siw.toeletta.model.TagliaCane;
import it.uniroma3.siw.toeletta.model.TipoPelo;
import it.uniroma3.siw.toeletta.model.Toelettatore;
import it.uniroma3.siw.toeletta.model.Utente;
import it.uniroma3.siw.toeletta.repository.CaneRepository;
import it.uniroma3.siw.toeletta.repository.FasciaOrariaRepository;
import it.uniroma3.siw.toeletta.repository.ServizioRepository;
import it.uniroma3.siw.toeletta.repository.ToelettatoreRepository;
import it.uniroma3.siw.toeletta.repository.UtenteRepository;

@Configuration
public class DataInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private CaneRepository caneRepository;

    @Autowired
    private ServizioRepository servizioRepository;

    @Autowired
    private ToelettatoreRepository toelettatoreRepository;

    @Autowired
    private FasciaOrariaRepository fasciaOrariaRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            creaServiziMancanti();

            if (utenteRepository.count() > 0) {
                return;
            }

            Utente admin = new Utente();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNome("Admin");
            admin.setCognome("ZampePulite");
            admin.setEmail("admin@zampepulite.it");
            admin.setTelefono("3330000000");
            admin.setRuolo(RuoloUtente.ADMIN);
            utenteRepository.save(admin);

            Utente mario = new Utente();
            mario.setUsername("mario");
            mario.setPassword(passwordEncoder.encode("mario123"));
            mario.setNome("Mario");
            mario.setCognome("Rossi");
            mario.setEmail("mario.rossi@email.it");
            mario.setTelefono("3331111111");
            mario.setRuolo(RuoloUtente.USER);
            utenteRepository.save(mario);

            Cane luna = new Cane();
            luna.setNome("Luna");
            luna.setRazza("Barboncino");
            luna.setTaglia(TagliaCane.PICCOLA);
            luna.setTipoPelo(TipoPelo.RICCIO);
            luna.setDataNascita(LocalDate.of(2021, 4, 12));
            luna.setNote("Molto tranquilla.");
            luna.setProprietario(mario);
            caneRepository.save(luna);

            Cane rex = new Cane();
            rex.setNome("Rex");
            rex.setRazza("Labrador");
            rex.setTaglia(TagliaCane.GRANDE);
            rex.setTipoPelo(TipoPelo.CORTO);
            rex.setDataNascita(LocalDate.of(2019, 9, 3));
            rex.setNote("Non ama il phon.");
            rex.setProprietario(mario);
            caneRepository.save(rex);

            Toelettatore giulia = new Toelettatore();
            giulia.setNome("Giulia");
            giulia.setCognome("Bianchi");
            giulia.setTelefono("3332222222");
            giulia.setSpecializzazione("Cani piccoli e pelo riccio");
            giulia.setAttivo(true);
            toelettatoreRepository.save(giulia);

            Toelettatore luca = new Toelettatore();
            luca.setNome("Luca");
            luca.setCognome("Verdi");
            luca.setTelefono("3333333333");
            luca.setSpecializzazione("Cani grandi");
            luca.setAttivo(true);
            toelettatoreRepository.save(luca);

            creaFascia(giulia, LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(10, 0));
            creaFascia(giulia, LocalDate.now().plusDays(1), LocalTime.of(10, 30), LocalTime.of(11, 30));
            creaFascia(giulia, LocalDate.now().plusDays(2), LocalTime.of(15, 0), LocalTime.of(16, 0));

            creaFascia(luca, LocalDate.now().plusDays(1), LocalTime.of(11, 0), LocalTime.of(12, 0));
            creaFascia(luca, LocalDate.now().plusDays(3), LocalTime.of(9, 30), LocalTime.of(10, 30));
            creaFascia(luca, LocalDate.now().plusDays(3), LocalTime.of(16, 0), LocalTime.of(17, 0));
        };
    }

    private void creaServiziMancanti() {
        creaServizioSeAssente(
            "Bagno completo",
            "Lavaggio, asciugatura e spazzolatura.",
            60,
            new BigDecimal("35.00")
        );
        creaServizioSeAssente(
            "Taglio pelo",
            "Taglio e rifinitura del manto.",
            90,
            new BigDecimal("50.00")
        );
        creaServizioSeAssente(
            "Taglio unghie",
            "Accorciamento e limatura unghie.",
            20,
            new BigDecimal("12.00")
        );
        creaServizioSeAssente(
            "Stripping",
            "Rimozione manuale del pelo morto per mantelli ruvidi.",
            120,
            new BigDecimal("65.00")
        );
        creaServizioSeAssente(
            "Trattamento cute sensibile",
            "Bagno delicato con prodotti specifici per cute sensibile.",
            60,
            new BigDecimal("40.00")
        );
        creaServizioSeAssente(
            "Snodatura pelo",
            "Rimozione nodi e spazzolatura approfondita del manto.",
            90,
            new BigDecimal("45.00")
        );
    }

    private void creaServizioSeAssente(String nome, String descrizione, Integer durataMinuti, BigDecimal prezzoBase) {
        if (servizioRepository.existsByNomeIgnoreCase(nome)) {
            return;
        }

        Servizio servizio = new Servizio();
        servizio.setNome(nome);
        servizio.setDescrizione(descrizione);
        servizio.setDurataMinuti(durataMinuti);
        servizio.setPrezzoBase(prezzoBase);
        servizio.setAttivo(true);
        servizioRepository.save(servizio);
    }

    private void creaFascia(Toelettatore toelettatore, LocalDate data, LocalTime oraInizio, LocalTime oraFine) {
        FasciaOraria fascia = new FasciaOraria();
        fascia.setToelettatore(toelettatore);
        fascia.setData(data);
        fascia.setOraInizio(oraInizio);
        fascia.setOraFine(oraFine);
        fascia.setDisponibile(true);
        fasciaOrariaRepository.save(fascia);
    }
}