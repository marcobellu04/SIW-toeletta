package it.uniroma3.siw.toeletta.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.siw.toeletta.model.FasciaOraria;
import it.uniroma3.siw.toeletta.model.Servizio;
import it.uniroma3.siw.toeletta.model.Toelettatore;
import it.uniroma3.siw.toeletta.service.FasciaOrariaService;
import it.uniroma3.siw.toeletta.service.ServizioService;
import it.uniroma3.siw.toeletta.service.ToelettatoreService;

@RestController
@RequestMapping("/api/react")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://127.0.0.1:5173",
    "http://localhost:5174",
    "http://127.0.0.1:5174"
})
public class ReactBookingApiController {

    private static final DateTimeFormatter DATE_LABEL_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private ServizioService servizioService;

    @Autowired
    private ToelettatoreService toelettatoreService;

    @Autowired
    private FasciaOrariaService fasciaOrariaService;

    @GetMapping("/booking-data")
    public BookingData bookingData() {
        List<ServizioDto> servizi = servizioService.findAttivi()
            .stream()
            .map(this::toServizioDto)
            .toList();

        List<ToelettatoreDto> toelettatori = toelettatoreService.findAttivi()
            .stream()
            .map(this::toToelettatoreDto)
            .toList();

        List<FasciaDto> fasce = fasciaOrariaService.findDisponibili()
            .stream()
            .filter(fascia -> fascia.getToelettatore() != null)
            .map(this::toFasciaDto)
            .toList();

        return new BookingData(servizi, toelettatori, fasce);
    }

    private ServizioDto toServizioDto(Servizio servizio) {
        return new ServizioDto(
            servizio.getId(),
            servizio.getNome(),
            servizio.getDescrizione(),
            servizio.getDurataMinuti(),
            servizio.getPrezzoBase().toString()
        );
    }

    private ToelettatoreDto toToelettatoreDto(Toelettatore toelettatore) {
        return new ToelettatoreDto(
            toelettatore.getId(),
            toelettatore.getNome(),
            toelettatore.getCognome(),
            toelettatore.getSpecializzazione()
        );
    }

    private FasciaDto toFasciaDto(FasciaOraria fascia) {
        return new FasciaDto(
            fascia.getId(),
            fascia.getData().toString(),
            fascia.getData().format(DATE_LABEL_FORMATTER),
            fascia.getOraInizio().toString(),
            fascia.getOraFine().toString(),
            fascia.getToelettatore().getId()
        );
    }

    public record BookingData(
        List<ServizioDto> servizi,
        List<ToelettatoreDto> toelettatori,
        List<FasciaDto> fasce
    ) {
    }

    public record ServizioDto(
        Long id,
        String nome,
        String descrizione,
        Integer durataMinuti,
        String prezzoBase
    ) {
    }

    public record ToelettatoreDto(
        Long id,
        String nome,
        String cognome,
        String specializzazione
    ) {
    }

    public record FasciaDto(
        Long id,
        String data,
        String dataLabel,
        String oraInizio,
        String oraFine,
        Long toelettatoreId
    ) {
    }
}
