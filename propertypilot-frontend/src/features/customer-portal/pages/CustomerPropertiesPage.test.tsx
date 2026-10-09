import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

const state = vi.hoisted(() => ({
  query: {
    data: [
      { propertyId: 'property-1', customerId: 'customer-1', title: 'Lakeview House', propertyType: 'House', listingStatus: 'OWNER_OCCUPIED', status: 'ACTIVE' },
      { propertyId: 'property-2', customerId: 'customer-1', title: 'Garden Flat', propertyType: 'Apartment', listingStatus: null, status: 'UNDER_VERIFICATION' },
    ],
    isLoading: false,
    isError: false,
    error: null,
    refetch: vi.fn(),
  },
}));

vi.mock('../hooks/useCustomerPortal', () => ({ useCustomerProperties: () => state.query }));

import CustomerPropertiesPage from './CustomerPropertiesPage';

afterEach(cleanup);
beforeEach(() => {
  state.query.isLoading = false;
  state.query.isError = false;
  state.query.error = null;
  state.query.data = [
    { propertyId: 'property-1', customerId: 'customer-1', title: 'Lakeview House', propertyType: 'House', listingStatus: 'OWNER_OCCUPIED', status: 'ACTIVE' },
    { propertyId: 'property-2', customerId: 'customer-1', title: 'Garden Flat', propertyType: 'Apartment', listingStatus: null, status: 'UNDER_VERIFICATION' },
  ];
});

describe('CustomerPropertiesPage', () => {
  it('shows human-readable property facts and links to the property detail', () => {
    render(<MemoryRouter><CustomerPropertiesPage /></MemoryRouter>);

    expect(screen.getByText('Lakeview House')).toBeTruthy();
    expect(screen.getByText('House')).toBeTruthy();
    expect(screen.getByRole('link', { name: 'View Lakeview House' }).getAttribute('href')).toBe('/customer/properties/property-1');
    expect(screen.queryByText('property-1')).toBeNull();
  });

  it('filters by property name and shows a clearable no-match state', () => {
    render(<MemoryRouter><CustomerPropertiesPage /></MemoryRouter>);
    fireEvent.change(screen.getByRole('textbox', { name: 'Search properties' }), { target: { value: 'garage' } });

    expect(screen.getByText('No properties match your search.')).toBeTruthy();
    fireEvent.click(screen.getByRole('button', { name: 'Clear filters' }));
    expect(screen.getByText('Lakeview House')).toBeTruthy();
  });

  it('shows loading and retryable error states', () => {
    state.query.isLoading = true;
    const { rerender } = render(<MemoryRouter><CustomerPropertiesPage /></MemoryRouter>);
    expect(screen.getByLabelText('Loading properties')).toBeTruthy();

    state.query.isLoading = false;
    state.query.isError = true;
    state.query.error = new Error('Properties unavailable');
    rerender(<MemoryRouter><CustomerPropertiesPage /></MemoryRouter>);
    expect(screen.getByText('Properties unavailable')).toBeTruthy();
    fireEvent.click(screen.getByRole('button', { name: 'Retry' }));
    expect(state.query.refetch).toHaveBeenCalled();
  });
});
