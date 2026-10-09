import { createTheme } from '@mui/material/styles';

export const propertyPilotTokens = {
  color: {
    navy: '#0B2D43',
    navyDark: '#071F31',
    navySoft: '#244458',
    teal: '#167F83',
    tealDark: '#0E696E',
    gold: '#B88A3B',
    canvas: '#F4F5F4',
    surface: '#FFFFFF',
    surfaceSubtle: '#F6F3EC',
    border: '#E1E4E2',
    text: '#142C3A',
    muted: '#5C6D77',
    success: '#15803D',
    warning: '#A64708',
    error: '#B42318',
    info: '#0369A1',
    disabled: '#718096',
  },
  spacing: {
    unitPx: 4,
    pageMobile: 2,
    pageDesktop: 3,
    section: 3,
    card: 2.5,
  },
  radius: {
    control: 10,
    card: 12,
    pill: 999,
  },
  elevation: {
    card: '0 2px 8px rgba(11, 45, 67, 0.055)',
    floating: '0 18px 44px rgba(7, 31, 49, 0.16)',
  },
  focus: {
    ring: '0 0 0 3px rgba(13, 148, 136, 0.32)',
    outline: '2px solid #0D9488',
  },
} as const;

export const theme = createTheme({
  spacing: 8,
  palette: {
    mode: 'light',
    primary: {
      main: propertyPilotTokens.color.navy,
      dark: propertyPilotTokens.color.navyDark,
      light: '#DCEAF2',
      contrastText: '#FFFFFF',
    },
    secondary: {
      main: propertyPilotTokens.color.teal,
      dark: propertyPilotTokens.color.tealDark,
      light: '#CCFBF1',
      contrastText: propertyPilotTokens.color.navy,
    },
    success: { main: propertyPilotTokens.color.success, light: '#DCFCE7', dark: '#166534' },
    warning: { main: propertyPilotTokens.color.warning, light: '#FEF3C7', dark: '#854D0E' },
    error: { main: propertyPilotTokens.color.error, light: '#FEE4E2', dark: '#912018' },
    info: { main: propertyPilotTokens.color.info, light: '#E0F2FE', dark: '#075985' },
    background: { default: propertyPilotTokens.color.canvas, paper: propertyPilotTokens.color.surface },
    text: { primary: propertyPilotTokens.color.text, secondary: propertyPilotTokens.color.muted, disabled: propertyPilotTokens.color.disabled },
    divider: propertyPilotTokens.color.border,
  },
  shape: { borderRadius: propertyPilotTokens.radius.card },
  typography: {
    fontFamily: 'Inter, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif',
    h1: { fontFamily: 'Georgia, "Times New Roman", serif', fontSize: 'clamp(1.875rem, 2.8vw, 2.25rem)', fontWeight: 700, lineHeight: 1.15, letterSpacing: '-0.025em' },
    h2: { fontFamily: 'Georgia, "Times New Roman", serif', fontSize: 'clamp(1.625rem, 2.35vw, 1.875rem)', fontWeight: 700, lineHeight: 1.2, letterSpacing: '-0.02em' },
    h3: { fontFamily: 'Georgia, "Times New Roman", serif', fontSize: 'clamp(1.5rem, 2vw, 1.625rem)', fontWeight: 700, lineHeight: 1.25, letterSpacing: '-0.015em' },
    h4: { fontFamily: 'Georgia, "Times New Roman", serif', fontSize: 'clamp(1.375rem, 1.8vw, 1.5rem)', fontWeight: 700, lineHeight: 1.3, letterSpacing: '-0.015em' },
    h5: { fontSize: '1.25rem', fontWeight: 700, lineHeight: 1.35, letterSpacing: '-0.015em' },
    h6: { fontSize: '1.0625rem', fontWeight: 700, lineHeight: 1.4, letterSpacing: '-0.01em' },
    body1: { fontSize: '0.9375rem', lineHeight: 1.55 },
    body2: { fontSize: '0.875rem', lineHeight: 1.5 },
    caption: { fontSize: '0.75rem', lineHeight: 1.45 },
    button: { textTransform: 'none', fontWeight: 650, letterSpacing: 0 },
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: {
        ':focus-visible': {
          outline: propertyPilotTokens.focus.outline,
          outlineOffset: 2,
        },
        'a:focus-visible, button:focus-visible, [role="button"]:focus-visible, input:focus-visible, select:focus-visible, textarea:focus-visible': {
          boxShadow: propertyPilotTokens.focus.ring,
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          border: `1px solid ${propertyPilotTokens.color.border}`,
          borderRadius: propertyPilotTokens.radius.card,
          boxShadow: propertyPilotTokens.elevation.card,
          backgroundImage: 'none',
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          minHeight: 44,
          borderRadius: propertyPilotTokens.radius.control,
          fontWeight: 650,
          '&.Mui-focusVisible': { boxShadow: propertyPilotTokens.focus.ring },
        },
        containedSecondary: {
          color: '#FFFFFF',
          backgroundColor: propertyPilotTokens.color.tealDark,
          '&:hover': { backgroundColor: '#115E59' },
        },
      },
    },
    MuiIconButton: {
      styleOverrides: {
        root: {
          '&.Mui-focusVisible': { boxShadow: propertyPilotTokens.focus.ring },
        },
      },
    },
    MuiOutlinedInput: {
      styleOverrides: {
        root: {
          borderRadius: propertyPilotTokens.radius.control,
          '&.Mui-focused': { boxShadow: propertyPilotTokens.focus.ring },
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: { borderRadius: 8, fontWeight: 650 },
      },
    },
    MuiListItemButton: {
      styleOverrides: {
        root: {
          minHeight: 44,
          '&.Mui-focusVisible': { boxShadow: propertyPilotTokens.focus.ring },
        },
      },
    },
  },
});
