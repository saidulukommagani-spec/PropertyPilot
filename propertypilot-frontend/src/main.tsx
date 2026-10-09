import React from 'react';
import ReactDOM from 'react-dom/client';
import { CssBaseline, ThemeProvider } from '@mui/material';
import { BrowserRouter } from 'react-router-dom';
import App from './App';
import { AuthProvider } from './features/auth/AuthContext';
import { theme } from './styles/theme';
import QueryProvider from './app/providers/QueryProvider';
import './styles/globals.css';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
        <QueryProvider><AuthProvider><App /></AuthProvider></QueryProvider>
      </BrowserRouter>
    </ThemeProvider>
  </React.StrictMode>,
);
