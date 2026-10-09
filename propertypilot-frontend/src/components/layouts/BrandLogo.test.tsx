import { describe, expect, it } from 'vitest';
import { cleanup, render, screen } from '@testing-library/react';
import { afterEach } from 'vitest';
import BrandLogo from './BrandLogo';

afterEach(cleanup);

describe('BrandLogo', () => {
  it.each([
    ['horizontal', '/brand/propertypilot-logo.svg'],
    ['horizontal-dark', '/brand/propertypilot-logo-dark.svg'],
    ['mark', '/brand/propertypilot-mark.svg'],
    ['mark-light', '/brand/propertypilot-mark-light.svg'],
    ['monochrome', '/brand/propertypilot-logo-monochrome.svg'],
  ] as const)('renders the %s vector asset with an accessible name', (variant, source) => {
    render(<BrandLogo variant={variant} />);

    expect(screen.getByRole('img', { name: 'PropertyPilot' }).getAttribute('src')).toBe(source);
  });
});
