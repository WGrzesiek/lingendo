import { withOpacity } from './with-opacity';

export const COLORS = {
  light: {
    primary: '#007AFF',
    secondary: '#5856D6',
    background: '#FFFFFF',
    foreground: '#000000',
    muted: '#F2F2F7',
    mutedForeground: '#8E8E93',
    destructive: '#FF3B30',
    border: '#C6C6C8',
    grey3: '#C7C7CC',
    grey4: '#D1D1D6',
    grey5: '#E5E5EA',
    grey6: '#F2F2F7',
  },
  dark: {
    primary: '#0A84FF',
    secondary: '#5E5CE6',
    background: '#000000',
    foreground: '#FFFFFF',
    muted: '#1C1C1E',
    mutedForeground: '#8E8E93',
    destructive: '#FF453A',
    border: '#38383A',
    grey3: '#48484A',
    grey4: '#3A3A3C',
    grey5: '#2C2C2E',
    grey6: '#1C1C1E',
  },
} as const;

export const ANDROID_RIPPLE = {
  dark: {
    primary: { color: withOpacity(COLORS.dark.grey3, 0.4), borderless: false },
    secondary: { color: withOpacity(COLORS.dark.grey5, 0.8), borderless: false },
    plain: { color: withOpacity(COLORS.dark.grey5, 0.8), borderless: false },
    tonal: { color: withOpacity(COLORS.dark.grey5, 0.8), borderless: false },
  },
  light: {
    primary: { color: withOpacity(COLORS.light.grey4, 0.4), borderless: false },
    secondary: { color: withOpacity(COLORS.light.grey5, 0.4), borderless: false },
    plain: { color: withOpacity(COLORS.light.grey5, 0.4), borderless: false },
    tonal: { color: withOpacity(COLORS.light.grey6, 0.4), borderless: false },
  },
};
