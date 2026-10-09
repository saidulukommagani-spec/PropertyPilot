import { Box, type SxProps, type Theme } from '@mui/material';

type BrandLogoProps = {
  variant: 'horizontal' | 'horizontal-dark' | 'stacked-dark' | 'mark' | 'mark-light' | 'monochrome';
  alt?: string;
  sx?: SxProps<Theme>;
};

const sources: Record<BrandLogoProps['variant'], string> = {
  horizontal: '/brand/propertypilot-logo.svg',
  'horizontal-dark': '/brand/propertypilot-logo-dark.svg',
  'stacked-dark': '/brand/propertypilot-logo-stacked-dark.png',
  mark: '/brand/propertypilot-mark.svg',
  'mark-light': '/brand/propertypilot-mark-light.svg',
  monochrome: '/brand/propertypilot-logo-monochrome.svg',
};

export default function BrandLogo({ variant, alt = 'PropertyPilot', sx }: BrandLogoProps) {
  return <Box component="img" src={sources[variant]} alt={alt} draggable={false} sx={{ display: 'block', ...sx }} />;
}
