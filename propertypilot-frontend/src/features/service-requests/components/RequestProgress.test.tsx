import { cleanup, render, screen } from '@testing-library/react';
import { afterEach, describe, expect, it } from 'vitest';
import RequestProgress from './RequestProgress';

afterEach(cleanup);

describe('RequestProgress', () => {
  it('communicates the current workflow stage in text', () => {
    render(<RequestProgress status="IN_PROGRESS" />);
    expect(screen.getByRole('region', { name: 'Service request progress' })).toBeTruthy();
    expect(screen.getByText('Service work is in progress.')).toBeTruthy();
    expect(screen.getByText(/Some services do not require an on-site visit/)).toBeTruthy();
  });

  it('shows terminal request states without presenting them as an active step', () => {
    render(<RequestProgress status="CANCELLED" />);
    expect(screen.getByRole('status').textContent).toContain('Request cancelled');
    expect(screen.queryByRole('region', { name: 'Service request progress' })).toBeNull();
  });
});
