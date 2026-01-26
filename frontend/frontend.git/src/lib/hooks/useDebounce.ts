import { useState, useEffect } from "react";

/**
 * Hook do debounce'owania wartości
 * Opóźnia aktualizację wartości o podany czas w milisekundach

 */
export function useDebounce<T>(value: T, delay: number = 300): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(timer);
    };
  }, [value, delay]);

  return debouncedValue;
}
