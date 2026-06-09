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

            Servizio bagno = new Servizio();
            bagno.setNome("Bagno completo");
            bagno.setDescrizione("Lavaggio, asciugatura e spazzolatura.");
            bagno.setDurataMinuti(60);
            bagno.setPrezzoBase(new BigDecimal("35.00"));
            bagno.setAttivo(true);
            servizioRepository.save(bagno);

            Servizio taglio = new Servizio();
            taglio.setNome("Taglio pelo");
            taglio.setDescrizione("Taglio e rifinitura del manto.");
            taglio.setDurataMinuti(90);
            taglio.setPrezzoBase(new BigDecimal("50.00"));
            taglio.setAttivo(true);
            servizioRepository.save(taglio);

            Servizio unghie = new Servizio();
            unghie.setNome("Taglio unghie");
            unghie.setDescrizione("Accorciamento e limatura unghie.");
            unghie.setDurataMinuti(20);
            unghie.setPrezzoBase(new BigDecimal("12.00"));
            unghie.setAttivo(true);
            servizioRepository.save(unghie);

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