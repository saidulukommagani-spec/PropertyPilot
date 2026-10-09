import { Component, type ErrorInfo, type ReactNode } from 'react';
import { Alert, Box, Button, Stack, Typography } from '@mui/material';

interface Props { children: ReactNode }
interface State { hasError: boolean }

export default class ServiceRequestWorkspaceErrorBoundary extends Component<Props, State> {
  state: State = { hasError: false };

  static getDerivedStateFromError(): State { return { hasError: true }; }

  componentDidCatch(error: Error, info: ErrorInfo) {
    console.error('Service request workspace render error', error, info.componentStack);
  }

  render() {
    if (this.state.hasError) return <Box role="alert" sx={{ maxWidth: 700, mx: 'auto', py: 5 }}>
      <Stack spacing={2}><Typography variant="h5" fontWeight={700}>This request workspace could not be displayed</Typography>
        <Alert severity="error">A display error interrupted this page. Reload the workspace to try again.</Alert>
        <Button variant="contained" onClick={() => window.location.reload()} sx={{ alignSelf: 'flex-start' }}>Reload workspace</Button>
      </Stack>
    </Box>;
    return this.props.children;
  }
}
