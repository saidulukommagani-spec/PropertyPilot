import { cleanup, render, screen } from '@testing-library/react';
import { afterEach, describe, expect, it, vi } from 'vitest';
import RouteErrorBoundary from './RouteErrorBoundary';

function BrokenRoute(): never {
  throw new Error('route render failed');
}

afterEach(() => {
  cleanup();
  vi.restoreAllMocks();
});

describe('RouteErrorBoundary', () => {
  it('shows a recoverable page error when a route throws', () => {
    vi.spyOn(console, 'error').mockImplementation(() => undefined);

    render(<RouteErrorBoundary><BrokenRoute /></RouteErrorBoundary>);

    expect(screen.getByRole('alert').textContent).toContain('This page could not be loaded.');
    expect(screen.getByRole('button', { name: 'Reload page' })).toBeTruthy();
  });
});
