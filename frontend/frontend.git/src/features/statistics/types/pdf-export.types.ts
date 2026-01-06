/**
 * Opcje eksportu statystyk do PDF
 */
export interface IPdfExportOptions {
  includeOverview: boolean;
  includeDailyPoints: boolean;
  includeMonthlyPoints: boolean;
  includeSessionStats: boolean;
  includeActivity: boolean;
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
  dateRange: "last-30-days",
};
