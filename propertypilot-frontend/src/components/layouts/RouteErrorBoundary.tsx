import { Component, type ErrorInfo, type ReactNode } from 'react';
import { Alert, Box, Button } from '@mui/material';

interface RouteErrorBoundaryProps { children: ReactNode }
interface RouteErrorBoundaryState { hasError: boolean }

export default class RouteErrorBoundary extends Component<RouteErrorBoundaryProps, RouteErrorBoundaryState> {
  state: RouteErrorBoundaryState = { hasError: false };

  static getDerivedStateFromError(): RouteErrorBoundaryState {
    return { hasError: true };
  }

  componentDidCatch(error: Error, _info: ErrorInfo) {
    // Keep error details in diagnostics without exposing them in the page.
    console.error('Route failed to render.', error);
  }

  render() {
    if (this.state.hasError) {
      return <Box sx={{ p: { xs: 2, md: 4 } }}><Alert severity="error" action={<Button color="inherit" size="small" onClick={() => window.location.reload()}>Reload page</Button>}>This page could not be loaded. Reload and try again.</Alert></Box>;
    }
    return this.props.children;
  }
}
