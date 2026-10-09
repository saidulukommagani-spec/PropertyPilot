import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen } from '@testing-library/react';
import VisitRescheduleDialog from './VisitRescheduleDialog';

afterEach(cleanup);

describe('VisitRescheduleDialog', () => {
  it('asks before discarding unsaved schedule changes and allows continuing to edit', () => {
    render(<VisitRescheduleDialog open scheduledAt="2026-10-12T10:00:00Z" isSaving={false} onClose={vi.fn()} onSubmit={vi.fn()} />);
    fireEvent.change(screen.getByRole('textbox', { name: 'Reason' }), { target: { value: 'Customer requested a new time' } });
    fireEvent.click(screen.getByRole('button', { name: 'Close' }));

    expect(screen.getByText('Unsaved schedule changes will be lost.')).toBeTruthy();
    fireEvent.click(screen.getByRole('button', { name: 'Keep editing' }));
    expect((screen.getByRole('textbox', { name: 'Reason' }) as HTMLTextAreaElement).value).toBe('Customer requested a new time');
    expect(screen.queryByText('Unsaved schedule changes will be lost.')).toBeNull();
  });

  it('discards unsaved changes only after explicit confirmation', () => {
    const onClose = vi.fn();
    render(<VisitRescheduleDialog open scheduledAt="2026-10-12T10:00:00Z" isSaving={false} onClose={onClose} onSubmit={vi.fn()} />);
    fireEvent.change(screen.getByRole('textbox', { name: 'Reason' }), { target: { value: 'Customer requested a new time' } });
    fireEvent.click(screen.getByRole('button', { name: 'Close' }));
    fireEvent.click(screen.getByRole('button', { name: 'Discard' }));

    expect(onClose).toHaveBeenCalledOnce();
    expect((screen.getByRole('textbox', { name: 'Reason' }) as HTMLTextAreaElement).value).toBe('');
  });
});
