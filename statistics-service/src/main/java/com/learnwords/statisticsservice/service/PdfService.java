package com.learnwords.statisticsservice.service;

import com.learnwords.statisticsservice.dto.PdfExportOptionsDto;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serwis odpowiedzialny za generowanie raportów PDF ze statystykami użytkownika.
 * Wykorzystuje Thymeleaf do renderowania HTML oraz OpenHTMLToPDF do konwersji na PDF.
 *
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-12-20
 */
@Slf4j
@Service
public class PdfService {

    private final TemplateEngine templateEngine;
    private final StatsService statsService;

    public PdfService(StatsService statsService) {
        this.statsService = statsService;
        this.templateEngine = createTemplateEngine();
    }

    /**
     * Konfiguruje i tworzy instancję Thymeleaf TemplateEngine.
     *
     * @return skonfigurowany TemplateEngine
     */
    private TemplateEngine createTemplateEngine() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
    }

    /**
     * Generuje raport PDF ze statystykami użytkownika z domyślnymi opcjami.
     *
     * @param userId identyfikator użytkownika
     * @return bajty wygenerowanego pliku PDF
     * @throws RuntimeException w przypadku błędu podczas generowania PDF
     */
    public byte[] generateStatsPdf(String userId) {
        return generateStatsPdf(userId, PdfExportOptionsDto.createDefault());
    }

    /**
     * Generuje raport PDF ze statystykami użytkownika z określonymi opcjami.
     *
     * @param userId  identyfikator użytkownika
     * @param options opcje eksportu określające, które sekcje uwzględnić
     * @return bajty wygenerowanego pliku PDF
     * @throws RuntimeException w przypadku błędu podczas generowania PDF
     */
    public byte[] generateStatsPdf(String userId, PdfExportOptionsDto options) {
        try {
            log.info("Rozpoczęcie generowania raportu PDF dla użytkownika: {} z opcjami: {}", userId, options);

            Map<String, Object> stats = statsService.getUserStats(userId);

            Context context = prepareTemplateContext(userId, stats, options);
            String html = templateEngine.process("stats-report", context);
            byte[] pdfBytes = convertHtmlToPdf(html);

            log.info("Pomyślnie wygenerowano raport PDF dla użytkownika: {} (rozmiar: {} bajtów)",
                    userId, pdfBytes.length);

            return pdfBytes;

        } catch (Exception e) {
            log.error("Błąd podczas generowania raportu PDF dla użytkownika: {}", userId, e);
            throw new RuntimeException("Nie udało się wygenerować raportu PDF", e);
        }
    }

    /**
     * Przygotowuje kontekst Thymeleaf z danymi do wygenerowania raportu.
     *
     * @param userId  identyfikator użytkownika
     * @param stats   mapa ze statystykami użytkownika
     * @param options opcje eksportu
     * @return kontekst Thymeleaf z danymi
     */
    private Context prepareTemplateContext(String userId, Map<String, Object> stats, PdfExportOptionsDto options) {
        Context context = new Context();

        context.setVariable("userId", userId);
        context.setVariable("generatedDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        context.setVariable("options", options);

        context.setVariable("stats", stats);

        if (options.includeMonthlyPoints()) {
            @SuppressWarnings("unchecked")
            Map<String, Long> pointsPerMonth = (Map<String, Long>) stats.getOrDefault("pointsPerMonth", new HashMap<>());

            List<Map.Entry<String, Long>> sortedMonthlyPoints = pointsPerMonth.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toList());

            context.setVariable("monthlyPoints", sortedMonthlyPoints);

            long maxPoints = pointsPerMonth.values().stream()
                    .max(Long::compareTo)
                    .orElse(1L);
            context.setVariable("maxPoints", maxPoints);
        }

        return context;
    }

    /**
     * Konwertuje HTML na PDF przy użyciu OpenHTMLToPDF.
     *
     * @param html zawartość HTML do konwersji
     * @return bajty pliku PDF
     * @throws Exception w przypadku błędu konwersji
     */
    private byte[] convertHtmlToPdf(String html) throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();
        }
    }
}
