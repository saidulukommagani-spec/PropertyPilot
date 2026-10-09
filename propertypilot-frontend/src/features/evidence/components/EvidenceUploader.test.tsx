import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';

const mocked = vi.hoisted(() => ({ mutateAsync: vi.fn(), isPending: false }));
vi.mock('../hooks/useEvidence', () => ({ useUploadEvidence: () => mocked }));

import EvidenceUploader from './EvidenceUploader';

const evidenceTypes = [{ evidenceTypeId: 'type-before', code: 'BEFORE_PHOTO', name: 'Before photo', category: 'Photos' }];

function chooseFiles(files: File[]) {
  const input = document.querySelector('input[type="file"]');
  if (!(input instanceof HTMLInputElement)) throw new Error('File input was not rendered');
  fireEvent.change(input, { target: { files } });
}

async function chooseEvidenceType() {
  fireEvent.mouseDown(screen.getByRole('combobox', { name: 'Evidence type' }));
  fireEvent.click(await screen.findByRole('option', { name: /Before photo/ }));
}

afterEach(() => {
  cleanup();
  mocked.isPending = false;
  vi.clearAllMocks();
});

beforeEach(() => {
  Object.defineProperty(URL, 'createObjectURL', { configurable: true, value: vi.fn(() => 'blob:preview') });
  Object.defineProperty(URL, 'revokeObjectURL', { configurable: true, value: vi.fn() });
});

describe('EvidenceUploader', () => {
  it('explains pending review and keeps the upload action unavailable until required fields are supplied', () => {
    render(<EvidenceUploader visitId="visit-1" evidenceTypes={evidenceTypes} />);
    expect(screen.getByText(/Evidence stays pending until Operations reviews it/)).toBeTruthy();
    expect((screen.getByRole('button', { name: 'Upload evidence' }) as HTMLButtonElement).disabled).toBe(true);
  });

  it('retains only failed and remaining files after a partial batch failure so retry does not duplicate successful uploads', async () => {
    const first = new File(['before'], 'before.jpg', { type: 'image/jpeg', lastModified: 10 });
    const second = new File(['after'], 'after.jpg', { type: 'image/jpeg', lastModified: 20 });
    mocked.mutateAsync.mockResolvedValueOnce({}).mockRejectedValueOnce(new Error('Upload network error')).mockResolvedValueOnce({});
    render(<EvidenceUploader visitId="visit-1" evidenceTypes={evidenceTypes} />);
    await chooseEvidenceType();
    chooseFiles([first, second]);
    fireEvent.click(screen.getByRole('button', { name: 'Upload evidence' }));

    expect(await screen.findByText(/1 earlier file was uploaded successfully; retry will continue with the remaining files/)).toBeTruthy();
    expect(screen.queryByText('before.jpg')).toBeNull();
    expect(screen.getByText('after.jpg')).toBeTruthy();
    fireEvent.click(screen.getByRole('button', { name: 'Upload evidence' }));
    await waitFor(() => expect(mocked.mutateAsync).toHaveBeenCalledTimes(3));
    expect(mocked.mutateAsync).toHaveBeenLastCalledWith(expect.objectContaining({ request: expect.objectContaining({ file: second }) }));
  });

  it('ignores duplicate file selections', async () => {
    const file = new File(['photo'], 'property.jpg', { type: 'image/jpeg', lastModified: 30 });
    render(<EvidenceUploader visitId="visit-1" evidenceTypes={evidenceTypes} />);
    chooseFiles([file]);
    chooseFiles([file]);
    expect(screen.getAllByText('property.jpg')).toHaveLength(1);
  });

  it('locks the evidence form while an upload is in progress', async () => {
    const file = new File(['photo'], 'active.jpg', { type: 'image/jpeg', lastModified: 40 });
    let finishUpload: (() => void) | undefined;
    mocked.mutateAsync.mockReturnValueOnce(new Promise<void>((resolve) => { finishUpload = resolve; }));
    render(<EvidenceUploader visitId="visit-1" evidenceTypes={evidenceTypes} />);
    await chooseEvidenceType();
    chooseFiles([file]);
    fireEvent.click(screen.getByRole('button', { name: 'Upload evidence' }));
    expect(await screen.findByRole('button', { name: 'Uploading 1 of 1' })).toBeTruthy();
    expect((screen.getByRole('button', { name: 'Remove' }) as HTMLButtonElement).disabled).toBe(true);
    const fileInput = document.querySelector('input[type="file"]');
    expect(fileInput instanceof HTMLInputElement && fileInput.disabled).toBe(true);
    finishUpload?.();
    await waitFor(() => expect(screen.queryByText('active.jpg')).toBeNull());
  });
});
