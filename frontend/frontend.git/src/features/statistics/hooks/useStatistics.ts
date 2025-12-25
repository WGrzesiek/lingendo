import { useMutation, useQuery } from "@tanstack/react-query";
import {
  exportStatisticsToPdf,
  getStatistics,
} from "../services/statistics.service";
import { IPdfExportOptions } from "../types/pdf-export.types";

/**
 * Hook do pobierania statystyk użytkownika
 */
export const useStatistics = () =>
  useQuery({
    queryKey: ["statistics"],
    queryFn: getStatistics,
  });

/**
 * Hook do eksportu statystyk do PDF
 * Automatycznie pobiera wygenerowany plik PDF
 */
export const useExportStatisticsToPdf = () => {
  return useMutation({
    mutationFn: async (options: IPdfExportOptions) => {
      const blob = await exportStatisticsToPdf(options);
      return blob;
    },
    onSuccess: (blob) => {
      // Automatycznie pobierz plik PDF
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;

      // Generuj nazwę pliku z aktualną datą
      const timestamp = new Date()
        .toISOString()
        .slice(0, 19)
        .replace(/:/g, "-");
      link.download = `statystyki_learnwords_${timestamp}.pdf`;

      document.body.appendChild(link);
      link.click();

      // Cleanup
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    },
  });
};
