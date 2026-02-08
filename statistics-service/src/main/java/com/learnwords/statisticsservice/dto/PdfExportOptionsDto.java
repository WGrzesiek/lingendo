package com.learnwords.statisticsservice.dto;

/**
 * DTO z opcjami eksportu statystyk do PDF.
 * Pozwala użytkownikowi wybrać, które sekcje mają być uwzględnione w raporcie.
 *
 * @param includeOverview         czy dołączyć podstawowe statystyki
 * @param includeDailyPoints      czy dołączyć wykres punktów dziennych
 * @param includeMonthlyPoints    czy dołączyć wykres punktów miesięcznych
 * @param includeSessionStats     czy dołączyć statystyki sesji
 * @param includeActivity         czy dołączyć historię aktywności
 * @param dateRange               okres danych do eksportu
 *
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-12-20
 */
public record PdfExportOptionsDto(
        boolean includeOverview,
        boolean includeDailyPoints,
        boolean includeMonthlyPoints,
        boolean includeSessionStats,
        boolean includeActivity,
        String dateRange
) {
    /**
     * Tworzy domyślne opcje eksportu (wszystkie sekcje włączone, cały okres).
     */
    public static PdfExportOptionsDto createDefault() {
        return new PdfExportOptionsDto(
                true,  // includeOverview
                false,  // includeDailyPoints
                true,  // includeMonthlyPoints
                true,  // includeSessionStats
                true,  // includeActivity
                "all-time"  // dateRange
        );
    }
}
