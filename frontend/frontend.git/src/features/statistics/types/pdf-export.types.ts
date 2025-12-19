/**
 * Opcje eksportu statystyk do PDF
 */
export interface IPdfExportOptions {
  /** Czy dołączyć podstawowe statystyki */
  includeOverview: boolean;
  /** Czy dołączyć wykres punktów dziennych */
  includeDailyPoints: boolean;
  /** Czy dołączyć wykres punktów miesięcznych */
  includeMonthlyPoints: boolean;
  /** Czy dołączyć statystyki sesji */
  includeSessionStats: boolean;
  /** Czy dołączyć historię aktywności */
  includeActivity: boolean;
  /** Czy dołączyć statystyki per kurs */
  includeDeckStats: boolean;
  /** Okres danych do eksportu */
  dateRange:
    | "last-7-days"
    | "last-30-days"
    | "last-3-months"
    | "last-year"
    | "all-time";
}

/**
 * Domyślne opcje eksportu
 */
export const defaultPdfExportOptions: IPdfExportOptions = {
  includeOverview: true,
  includeDailyPoints: true,
  includeMonthlyPoints: false,
  includeSessionStats: true,
  includeActivity: true,
  includeDeckStats: false,
  dateRange: "last-30-days",
};
