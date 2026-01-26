import { useMutation, useQuery } from "@tanstack/react-query";
import {
  exportStatisticsToPdf,
  getStatistics,
} from "../services/statistics.service";
import { IPdfExportOptions } from "../types/pdf-export.types";
import { qk } from "@/lib/queryKeys";

/**
 * Hook do pobierania statystyk użytkownika
 */
export const useStatistics = () =>
  useQuery({
    queryKey: qk.statistics.all,
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
      // Automatycznie pobieranie pliku
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;

      const timestamp = new Date()
        .toISOString()
        .slice(0, 19)
        .replace(/:/g, "-");
      link.download = `statystyki_lingendo_${timestamp}.pdf`;

      document.body.appendChild(link);
      link.click();

      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    },
  });
};
