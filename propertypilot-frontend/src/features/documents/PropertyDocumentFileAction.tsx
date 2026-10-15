import { useEffect, useState } from 'react';
import { Alert, Button, Dialog, DialogActions, DialogContent, DialogTitle, Skeleton, Typography } from '@mui/material';
import VisibilityRounded from '@mui/icons-material/VisibilityRounded';
import { apiClient } from '../../api/client';

export default function PropertyDocumentFileAction({ documentId, fileName }: { documentId: string; fileName?: string }) {
  const [open, setOpen] = useState(false);
  const [url, setUrl] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [mimeType, setMimeType] = useState('');

  useEffect(() => () => { if (url) URL.revokeObjectURL(url); }, [url]);

  const showFile = async () => {
    setOpen(true); setLoading(true); setError('');
    try {
      const response = await apiClient.get<Blob>(`/api/v1/documents/${documentId}/file`, { responseType: 'blob' });
      setMimeType(response.data.type);
      setUrl(URL.createObjectURL(response.data));
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'The document file could not be loaded.');
    } finally { setLoading(false); }
  };

  return <>
    <Button size="small" startIcon={<VisibilityRounded />} onClick={() => void showFile()}>View file</Button>
    <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="md" aria-labelledby={`property-document-${documentId}`}>
      <DialogTitle id={`property-document-${documentId}`}>{fileName || 'Property document'}</DialogTitle>
      <DialogContent dividers sx={{ minHeight: 360, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
        {loading && <Skeleton variant="rounded" width="100%" height={320} />}
        {error && <Alert severity="error">{error}</Alert>}
        {!loading && !error && url && (mimeType === 'application/pdf' || mimeType.startsWith('image/') || mimeType.startsWith('text/')) && <iframe title={fileName || 'Property document preview'} src={url} style={{ width: '100%', height: '70vh', border: 0 }} />}
        {!loading && !error && url && !(mimeType === 'application/pdf' || mimeType.startsWith('image/') || mimeType.startsWith('text/')) && <Typography color="text.secondary">This file type cannot be previewed in the browser. Use Download file to open it on your device.</Typography>}
      </DialogContent>
      <DialogActions>{url && !loading && <Button component="a" href={url} download={fileName || 'property-document'}>Download file</Button>}<Button onClick={() => setOpen(false)}>Close</Button></DialogActions>
    </Dialog>
  </>;
}
