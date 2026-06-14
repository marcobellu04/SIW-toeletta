# Frontend React ZampePulite

Questa cartella contiene la parte React separata dal backend Spring Boot.

## Avvio

1. Avvia il backend Spring Boot su `http://localhost:8081`.
2. Entra nella cartella `frontend`.
3. Installa le dipendenze:

```bash
npm install
```

4. Avvia React:

```bash
npm run dev
```

Il frontend React sara disponibile su `http://localhost:5173`.

## Collegamento con Spring Boot

React legge i dati da questa API del backend:

```text
http://localhost:8081/api/react/booking-data
```

Il backend espone servizi, toelettatori e fasce disponibili in JSON. React usa questi dati per filtrare dinamicamente gli orari in base a servizio, toelettatore e giorno.
