import React from 'react';
import { createRoot } from 'react-dom/client';
import App from './App.jsx';
import './App.css';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { error: null };
  }

  static getDerivedStateFromError(error) {
    return { error };
  }

  render() {
    if (this.state.error) {
      return (
        <main style={{ padding: 32, fontFamily: 'Segoe UI, sans-serif' }}>
          <h1 style={{ color: '#9f1d1d' }}>Qualcosa non ha funzionato</h1>
          <pre style={{ whiteSpace: 'pre-wrap' }}>{this.state.error.message}</pre>
        </main>
      );
    }

    return this.props.children;
  }
}

window.addEventListener('error', (event) => {
  const root = document.getElementById('root');
  root.innerHTML = `
    <main style="padding:32px;font-family:Segoe UI,sans-serif">
      <h1 style="color:#9f1d1d">Qualcosa non ha funzionato</h1>
      <pre style="white-space:pre-wrap">${event.message}</pre>
    </main>
  `;
});

createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <ErrorBoundary>
      <App />
    </ErrorBoundary>
  </React.StrictMode>
);
