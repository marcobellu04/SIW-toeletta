import React, { useEffect, useMemo, useState } from 'react';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081';
const DEMO_DATA = {
  servizi: [
    {
      id: 1,
      nome: 'Bagno completo',
      descrizione: 'Lavaggio, asciugatura e spazzolatura del cane.',
      durataMinuti: 60,
      prezzoBase: '30.00'
    },
    {
      id: 2,
      nome: 'Taglio pelo',
      descrizione: 'Taglio e rifinitura in base al tipo di pelo.',
      durataMinuti: 90,
      prezzoBase: '45.00'
    }
  ],
  toelettatori: [
    {
      id: 1,
      nome: 'Giulia',
      cognome: 'Bianchi',
      specializzazione: 'Tagli e manti lunghi'
    },
    {
      id: 2,
      nome: 'Luigi',
      cognome: 'Rossi',
      specializzazione: 'Bagno e trattamenti cute'
    }
  ],
  fasce: [
    { id: 1, data: '2026-06-15', dataLabel: '15/06/2026', oraInizio: '09:00', oraFine: '09:30', toelettatoreId: 1 },
    { id: 2, data: '2026-06-15', dataLabel: '15/06/2026', oraInizio: '09:30', oraFine: '10:00', toelettatoreId: 1 },
    { id: 3, data: '2026-06-15', dataLabel: '15/06/2026', oraInizio: '10:00', oraFine: '10:30', toelettatoreId: 1 },
    { id: 4, data: '2026-06-15', dataLabel: '15/06/2026', oraInizio: '14:00', oraFine: '14:30', toelettatoreId: 2 },
    { id: 5, data: '2026-06-15', dataLabel: '15/06/2026', oraInizio: '14:30', oraFine: '15:00', toelettatoreId: 2 }
  ]
};

function App() {
  const [bookingData, setBookingData] = useState(DEMO_DATA);
  const [loading, setLoading] = useState(false);
  const [errore, setErrore] = useState('');
  const [usaDatiDemo, setUsaDatiDemo] = useState(true);
  const [servizioId, setServizioId] = useState('');
  const [toelettatoreId, setToelettatoreId] = useState('');
  const [giorno, setGiorno] = useState('');
  const [fasciaSelezionataId, setFasciaSelezionataId] = useState('');

  useEffect(() => {
    fetch(`${API_BASE_URL}/api/react/booking-data`)
      .then((response) => {
        if (!response.ok) {
          throw new Error('Al momento non riusciamo a caricare le disponibilita aggiornate.');
        }
        return response.json();
      })
      .then((data) => {
        setBookingData({
          servizi: Array.isArray(data.servizi) ? data.servizi : [],
          toelettatori: Array.isArray(data.toelettatori) ? data.toelettatori : [],
          fasce: Array.isArray(data.fasce) ? data.fasce : []
        });
        setErrore('');
        setUsaDatiDemo(false);
        setLoading(false);
      })
      .catch((error) => {
        setErrore(error.message);
        setBookingData(DEMO_DATA);
        setUsaDatiDemo(true);
        setLoading(false);
      });
  }, []);

  const servizioSelezionato = useMemo(() => {
    return bookingData.servizi.find((servizio) => String(servizio.id) === servizioId);
  }, [bookingData.servizi, servizioId]);

  const giorniDisponibili = useMemo(() => {
    const giorni = bookingData.fasce
      .filter((fascia) => !toelettatoreId || String(fascia.toelettatoreId) === toelettatoreId)
      .map((fascia) => fascia.data);

    return Array.from(new Set(giorni)).sort();
  }, [bookingData.fasce, toelettatoreId]);

  const disponibilita = useMemo(() => {
    if (!servizioSelezionato || !toelettatoreId || !giorno) {
      return [];
    }

    const slots = bookingData.fasce
      .filter((fascia) => String(fascia.toelettatoreId) === toelettatoreId && fascia.data === giorno)
      .sort((a, b) => a.oraInizio.localeCompare(b.oraInizio));

    return slots
      .map((fascia, index) => {
        const oraFineCalcolata = calcolaFinePrenotazione(slots, index, servizioSelezionato.durataMinuti);

        if (!oraFineCalcolata) {
          return null;
        }

        return {
          ...fascia,
          oraFineCalcolata
        };
      })
      .filter(Boolean);
  }, [bookingData.fasce, servizioSelezionato, toelettatoreId, giorno]);

  function resetSlot() {
    setFasciaSelezionataId('');
  }

  function prenotaNelSitoSpring() {
    const query = servizioId ? `?servizioId=${servizioId}` : '';
    window.location.href = `${API_BASE_URL}/prenotazioni/nuova${query}`;
  }

  return (
    <main className="app-shell">
      <section className="hero-panel">
        <div>
          <p className="eyebrow">Prenotazione online</p>
          <h1>ZampePulite</h1>
          <p>
            Scegli il servizio, il toelettatore e il giorno: ti mostriamo solo gli orari disponibili.
          </p>
        </div>

        <a className="back-link" href={API_BASE_URL}>
          Torna al sito
        </a>
      </section>

      {loading && <div className="state-box">Caricamento disponibilita...</div>}
      {errore && (
        <div className="state-box state-warning">
          Non riusciamo a caricare le disponibilita aggiornate. Riprova tra poco.
        </div>
      )}

      {!loading && (
        <section className="booking-layout">
          <form className="control-panel">
            <div className="field">
              <label htmlFor="servizio">Servizio</label>
              <select
                id="servizio"
                value={servizioId}
                onChange={(event) => {
                  setServizioId(event.target.value);
                  resetSlot();
                }}
              >
                <option value="">Scegli servizio</option>
                {bookingData.servizi.map((servizio) => (
                  <option key={servizio.id} value={servizio.id}>
                    {servizio.nome} - {servizio.durataMinuti} min - {servizio.prezzoBase} euro
                  </option>
                ))}
              </select>
            </div>

            <div className="field">
              <label htmlFor="toelettatore">Toelettatore</label>
              <select
                id="toelettatore"
                value={toelettatoreId}
                onChange={(event) => {
                  setToelettatoreId(event.target.value);
                  setGiorno('');
                  resetSlot();
                }}
              >
                <option value="">Scegli toelettatore</option>
                {bookingData.toelettatori.map((toelettatore) => (
                  <option key={toelettatore.id} value={toelettatore.id}>
                    {toelettatore.nome} {toelettatore.cognome}
                  </option>
                ))}
              </select>
            </div>

            <div className="field">
              <label htmlFor="giorno">Giorno</label>
              <select
                id="giorno"
                value={giorno}
                onChange={(event) => {
                  setGiorno(event.target.value);
                  resetSlot();
                }}
              >
                <option value="">Scegli giorno</option>
                {giorniDisponibili.map((data) => (
                  <option key={data} value={data}>
                    {formattaData(data)}
                  </option>
                ))}
              </select>
            </div>

            {servizioSelezionato && (
              <div className="service-summary">
                <strong>{servizioSelezionato.nome}</strong>
                <span>{servizioSelezionato.descrizione}</span>
              </div>
            )}
          </form>

          <section className="availability-panel">
            <div className="panel-heading">
              <div>
                <h2>Orari disponibili</h2>
                <p>Seleziona una fascia oraria compatibile con la durata del servizio scelto.</p>
              </div>
              <span className="counter">{disponibilita.length}</span>
            </div>

            {disponibilita.length === 0 ? (
              <div className="empty-result">
                {servizioSelezionato && toelettatoreId && giorno
                  ? 'Nessuno slot disponibile per le scelte selezionate.'
                  : 'Seleziona servizio, toelettatore e giorno per vedere gli slot disponibili.'}
              </div>
            ) : (
              <div className="slot-grid">
                {disponibilita.map((fascia) => (
                  <button
                    className={fasciaSelezionataId === fascia.id ? 'slot-card selected' : 'slot-card'}
                    key={fascia.id}
                    type="button"
                    onClick={() => setFasciaSelezionataId(fascia.id)}
                  >
                    <span>{fascia.oraInizio} - {fascia.oraFineCalcolata}</span>
                    <small>{fascia.dataLabel}</small>
                  </button>
                ))}
              </div>
            )}

            <button
              className="primary-action"
              type="button"
              disabled={!fasciaSelezionataId}
              onClick={prenotaNelSitoSpring}
            >
              Continua la prenotazione
            </button>
          </section>
        </section>
      )}
    </main>
  );
}

function calcolaFinePrenotazione(slots, startIndex, durataRichiesta) {
  const primo = slots[startIndex];
  let minutiCoperti = 0;
  let fine = primo.oraFine;

  for (let i = startIndex; i < slots.length; i++) {
    const slot = slots[i];

    if (slot.data !== primo.data) {
      return null;
    }

    if (i > startIndex && slots[i - 1].oraFine !== slot.oraInizio) {
      return null;
    }

    minutiCoperti += minutiTra(slot.oraInizio, slot.oraFine);
    fine = slot.oraFine;

    if (minutiCoperti >= durataRichiesta) {
      return fine;
    }
  }

  return null;
}

function minutiTra(inizio, fine) {
  const [inizioOre, inizioMinuti] = inizio.split(':').map(Number);
  const [fineOre, fineMinuti] = fine.split(':').map(Number);

  return fineOre * 60 + fineMinuti - (inizioOre * 60 + inizioMinuti);
}

function formattaData(data) {
  return new Intl.DateTimeFormat('it-IT', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  }).format(new Date(`${data}T00:00:00`));
}

export default App;
