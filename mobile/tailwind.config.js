/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './App.{js,ts,tsx}',
    './app/**/*.{js,ts,tsx}',
    './components/**/*.{js,ts,tsx}',
    './src/**/*.{js,ts,tsx}',
  ],

  presets: [require('nativewind/preset')],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#22c55e', // Główny zielony
          foreground: '#ffffff',
          light: '#dcfce7',
          dark: '#16a34a',
        },
        secondary: {
          DEFAULT: '#f0fdf4',
          foreground: '#1a1a1a',
        },
        background: '#fafdfb',
        foreground: '#1a1a1a',
        card: {
          DEFAULT: '#ffffff',
          foreground: '#1a1a1a',
        },
        muted: {
          DEFAULT: '#f4f4f5',
          foreground: '#71717a',
        },
        accent: {
          DEFAULT: '#f0fdf4',
          foreground: '#1a1a1a',
        },
        destructive: {
          DEFAULT: '#ef4444',
          foreground: '#ffffff',
        },
        border: '#e4e4e7',
        input: '#e4e4e7',
        ring: '#22c55e',
        // Kolory funkcjonalne
        success: {
          DEFAULT: '#22c55e',
          foreground: '#ffffff',
          light: '#dcfce7',
        },
        warning: {
          DEFAULT: '#f59e0b',
          foreground: '#ffffff',
          light: '#fef3c7',
        },
        info: {
          DEFAULT: '#3b82f6',
          foreground: '#ffffff',
          light: '#dbeafe',
        },
        error: {
          DEFAULT: '#ef4444',
          foreground: '#ffffff',
          light: '#fee2e2',
        },

        streak: '#f59e0b',
        premium: '#8b5cf6',
      },
      borderRadius: {
        sm: '0.375rem',
        md: '0.5rem',
        lg: '0.625rem',
        xl: '1rem',
      },
    },
  },
  plugins: [],
};
