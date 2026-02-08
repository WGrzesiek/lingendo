package com.learnwords.statisticsservice.service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;

import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.*;
import com.learnwords.statisticsservice.dto.PdfExportOptionsDto;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serwis odpowiedzialny za generowanie raportow PDF ze statystykami uzytkownika.
 * Wykorzystuje Thymeleaf do renderowania HTML oraz OpenHTMLToPDF do konwersji na PDF.
 *
 * @author Grzegorz Wawrzen
 * @version 1.0
 * @since 2025-12-20
 */
@Slf4j
@Service
public class PdfService {

    private Map<String, Object> stats() {
     Map<String,Object> stats = new HashMap<>();
        stats.put("streak", 5L);
        stats.put("totalPoints", 1240L);
        stats.put("sessionsCompleted", 2L);
        stats.put("enrolledDecks", 1L);
        stats.put("createdDecks", 7L);
        stats.put("completedDecks", 1L);
        stats.put("flashcardsCreated", 267L);
        stats.put("flashcardsAnswered", 267L);
        stats.put("flashcardsAnsweredCorrectly", 196L);
        stats.put( "averageAnswersPerSession", 89.0);
        stats.put("pointsPerMonth", Map.of("2025-11", 10L, "2025-12", 1240L));
        return stats;
}


    private final StatsService statsService;

    private static final DeviceRgb GREEN_DARK = new DeviceRgb(22, 101, 52);
    private static final DeviceRgb GREEN_MAIN = new DeviceRgb(34, 197, 94);
    private static final DeviceRgb GREEN_DEEP = new DeviceRgb(21, 128, 61);
    private static final DeviceRgb GRAY_TEXT = new DeviceRgb(55, 65, 81);
    private static final DeviceRgb GRAY_LIGHT = new DeviceRgb(243, 244, 246);

    public PdfService(StatsService statsService) {
        this.statsService = statsService;
    }

    public byte[] generateStatsPdf(String userId) {
        return generateStatsPdf(userId, PdfExportOptionsDto.createDefault());
    }

    public byte[] generateStatsPdf(String userId, PdfExportOptionsDto options) {
        try {
            log.info("Generowanie raportu PDF (iText+JFreeChart) dla uzytkownika: {}, opcje: {}",
                    userId, options);

            Map<String, Object> stats = getStatsForDateRange(userId, options.dateRange());
            stats.put("userId", userId);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            pdf.setDefaultPageSize(PageSize.A4);
            Document doc = new Document(pdf);

            PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA

            );
            PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD
            );
            doc.setFont(fontRegular);
            doc.setFontSize(10);

            addHeader(doc, fontBold);

            addMetadata(doc, userId, options.dateRange(), fontRegular);

            if (options.includeOverview()) {
                addOverviewSection(doc, stats, fontBold);
            }

            if (options.includeSessionStats()) {
                addFlashcardsSection(doc, stats, fontBold);
            }

            if (options.includeDailyPoints()) {
                addDailyPointsChartSection(doc, userId, stats);
            }

            if (options.includeMonthlyPoints()) {
                addMonthlyPointsChartSection(doc, stats);
            }

            if (options.includeActivity()) {
//                addActivityHistorySection(doc, stats, fontBold);
                addDetailedTableSection(doc, stats, fontBold);
            }

            addFooter(doc);

            doc.close();
            pdf.close();

            byte[] pdfBytes = baos.toByteArray();
            log.info("Wygenerowano PDF dla {} ({} bajtow)", userId, pdfBytes.length);
            return pdfBytes;
        } catch (Exception e) {
            log.error("Blad generowania PDF", e);
            throw new RuntimeException("Nie udalo sie wygenerowac raportu PDF", e);
        }
    }

    // === SEKCJE ===

    private void addHeader(Document doc, PdfFont fontBold) {
        Paragraph title = new Paragraph("📊 Raport Statystyk LearnWords")
                .setFont(fontBold)
                .setFontSize(18)
                .setFontColor(GREEN_DARK)
                .setTextAlignment(TextAlignment.CENTER);

        Paragraph subtitle = new Paragraph("Szczegolowe zestawienie Twojej aktywnosci edukacyjnej")
                .setFontSize(10)
                .setFontColor(GRAY_TEXT)
                .setTextAlignment(TextAlignment.CENTER);

        doc.add(title);
        doc.add(subtitle);

        LineSeparator line = new LineSeparator(new SolidLine());
        line.setStrokeColor(GREEN_MAIN);
        line.setMarginTop(8);
        line.setMarginBottom(12);
        doc.add(line);
    }

    private void addMetadata(Document doc, String userId, String dateRange, PdfFont fontRegular) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String periodLabel = getDateRangeLabel(dateRange);

        Table meta = new Table(UnitValue.createPercentArray(new float[]{2, 3, 2}))
                .setWidth(UnitValue.createPercentValue(100));

        meta.addCell(metaCell("Data wygenerowania:", date));
        meta.addCell(metaCell("Uzytkownik:", userId));
        meta.addCell(metaCell("Okres:", periodLabel));

        doc.add(meta);
        doc.add(new Paragraph("\n"));
    }

    private Cell metaCell(String label, String value) {
        Paragraph p = new Paragraph()
                .add(new Text(label).setBold())
                .add(" ")
                .add(new Text(value));

        return new Cell()
                .setBorder(Border.NO_BORDER)
                .add(p)
                .setFontSize(9)
                .setTextAlignment(TextAlignment.LEFT);
    }

    private void addOverviewSection(Document doc, Map<String, Object> stats, PdfFont fontBold) {
        doc.add(sectionTitle("Przeglad kluczowych statystyk", fontBold));

        Table grid = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(10);

        long streak = getLong(stats, "streak");
        long totalPoints = getLong(stats, "totalPoints");
        long sessionsCompleted = getLong(stats, "sessionsCompleted");
        long enrolledDecks = getLong(stats, "enrolledDecks");
        long createdDecks = getLong(stats, "createdDecks");
        long completedDecks = getLong(stats, "completedDecks");

        grid.addCell(statCard("🔥 Seria dni nauki", streak));
        grid.addCell(statCard("⭐ Laczne punkty", totalPoints));
        grid.addCell(statCard("✅ Ukonczone sesje", sessionsCompleted));
        grid.addCell(statCard("📚 Zapisane talie", enrolledDecks));
        grid.addCell(statCard("🎯 Utworzone talie", createdDecks));
        grid.addCell(statCard("💯 Ukonczone talie", completedDecks));

        doc.add(grid);
    }

    private Cell statCard(String label, long value) {
        Paragraph labelP = new Paragraph(label)
                .setFontSize(9)
                .setFontColor(new DeviceRgb(240, 253, 244));

        Paragraph valueP = new Paragraph(String.valueOf(value))
                .setFontSize(16)
                .setBold()
                .setFontColor(DeviceRgb.WHITE);

        Cell cell = new Cell()
                .add(labelP)
                .add(valueP)
                .setPadding(10)
                .setBackgroundColor(GREEN_MAIN)
                .setBorderRadius(new BorderRadius(6))
                .setBorder(Border.NO_BORDER);

        return cell;
    }

    private void addFlashcardsSection(Document doc, Map<String, Object> stats, PdfFont fontBold) {

        doc.add(sectionTitle("Statystyki fiszek", fontBold));

        long flashcardsCreated = getLong(stats, "flashcardsCreated");
        long flashcardsAnswered = getLong(stats, "flashcardsAnswered");
        long flashcardsCorrect = getLong(stats, "flashcardsAnsweredCorrectly");
        double avgAnswersPerSession = getDouble(stats, "averageAnswersPerSession");

        double accuracy = flashcardsAnswered > 0
                ? (flashcardsCorrect * 100.0 / flashcardsAnswered)
                : 0.0;

        Paragraph summary = new Paragraph()
                .add("Utworzyles ")
                .add(new Text(String.valueOf(flashcardsCreated)).setBold())
                .add(" fiszek i odpowiedziales na ")
                .add(new Text(String.valueOf(flashcardsAnswered)).setBold())
                .add(" fiszek, z czego ")
                .add(new Text(String.valueOf(flashcardsCorrect)).setBold())
                .add(" poprawnie.");

        Cell highlight = new Cell()
                .add(summary)
                .setBackgroundColor(new DeviceRgb(236, 252, 243))
                .setBorderLeft(new com.itextpdf.layout.borders.SolidBorder(GREEN_MAIN, 3))
                .setBorderTop(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER)
                .setBorderBottom(Border.NO_BORDER)
                .setPadding(8)
                .setBorderRadius(new BorderRadius(4));

        Table highlightTable = new Table(UnitValue.createPercentArray(1))
                .setWidth(UnitValue.createPercentValue(100))
                .addCell(highlight)
                .setMarginBottom(8);

        doc.add(highlightTable);


        Table t = new Table(UnitValue.createPercentArray(new float[]{2, 5}))
                .setWidth(UnitValue.createPercentValue(100));

        t.addCell(progressLabelCell("Skutecznosc odpowiedzi",
                String.format(Locale.ROOT, "%.1f%% (%d / %d)", accuracy, flashcardsCorrect, flashcardsAnswered)));

        t.addCell(progressBarCell(accuracy / 100.0));

        t.addCell(progressLabelCell("Srednia odpowiedzi na sesje",
                String.format(Locale.ROOT, "%.1f", avgAnswersPerSession)));

        double avgScale = Math.min(avgAnswersPerSession / 100.0, 1.0);
        t.addCell(progressBarCell(avgScale));

        doc.add(t);
        doc.add(new Paragraph("\n"));
    }

    private Cell progressLabelCell(String label, String value) {
        Paragraph p = new Paragraph()
                .add(new Text(label).setBold())
                .add("\n")
                .add(new Text(value).setFontSize(9).setFontColor(GRAY_TEXT));

        return new Cell()
                .add(p)
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPaddingRight(4);
    }

    private Cell progressBarCell(double ratio) {
        ratio = Math.max(0, Math.min(1, ratio));

        Table bar = new Table(UnitValue.createPercentArray(1))
                .setWidth(UnitValue.createPercentValue(100))
                .setHeight(10)
                .setBackgroundColor(GRAY_LIGHT)
                .setBorder(Border.NO_BORDER);

        float percent = (float) (ratio * 100.0);

        Cell fill = new Cell()
                .setBackgroundColor(GREEN_MAIN)
                .setBorder(Border.NO_BORDER)
                .setWidth(UnitValue.createPercentValue(percent));

        bar.addCell(fill);

        return new Cell()
                .add(bar)
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private void addMonthlyPointsChartSection(Document doc, Map<String, Object> stats) throws Exception {

        Map<String, Long> pointsPerMonth = (Map<String, Long>) stats.getOrDefault("pointsPerMonth", new HashMap<>());

        if (pointsPerMonth.isEmpty()) {
            return;
        }

        Paragraph title = sectionTitle("Punkty w poszczegolnych miesiacach", null);
        doc.add(title);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        List<Map.Entry<String, Long>> sorted = pointsPerMonth.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());

        for (Map.Entry<String, Long> entry : sorted) {
            dataset.addValue(entry.getValue(), "Punkty", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                null,
                "Miesiac",
                "Punkty",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                false,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        chart.getCategoryPlot().setBackgroundPaint(new Color(249, 250, 251));
        chart.getCategoryPlot().getRenderer().setSeriesPaint(0, new Color(34, 197, 94));
        chart.getCategoryPlot().setOutlineVisible(false);

        int width = 500;
        int height = 200;
        BufferedImage bufferedImage = chart.createBufferedImage(width, height);

        ByteArrayOutputStream imageBaos = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(bufferedImage, "png", imageBaos);
        ImageData imageData = ImageDataFactory.create(imageBaos.toByteArray());
        Image image = new Image(imageData);
        image.setAutoScale(true);
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);

        doc.add(image);
        doc.add(new Paragraph("\n"));
    }

    private void addDetailedTableSection(Document doc, Map<String, Object> stats, PdfFont fontBold) {
        doc.add(sectionTitle("Szczegolowe zestawienie", fontBold));

//        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1, 6}))
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(headerCell("Kategoria"));
        table.addHeaderCell(headerCell("Wartosc"));
        table.addHeaderCell(headerCell("Opis"));

        addRow(table, "Seria dni nauki", getLong(stats, "streak"),
                "Liczba kolejnych dni nauki");

        addRow(table, "laczne punkty", getLong(stats, "totalPoints"),
                "Suma wszystkich zdobytych punktow");

        addRow(table, "Utworzone talie", getLong(stats, "createdDecks"),
                "Liczba talii utworzonych przez Ciebie");

        addRow(table, "Zapisane talie", getLong(stats, "enrolledDecks"),
                "Liczba talii, do ktorych jestes zapisany");

        addRow(table, "Ukonczone talie", getLong(stats, "completedDecks"),
                "Liczba w pelni ukonczonych talii");

        addRow(table, "Ukonczone sesje", getLong(stats, "sessionsCompleted"),
                "Liczba przeprowadzonych sesji nauki");

        addRow(table, "Utworzone fiszki", getLong(stats, "flashcardsCreated"),
                "Liczba fiszek dodanych przez Ciebie");

        addRow(table, "Odpowiedziane fiszki", getLong(stats, "flashcardsAnswered"),
                "Liczba fiszek, na ktore udzieliles odpowiedzi");

        addRow(table, "Poprawne odpowiedzi", getLong(stats, "flashcardsAnsweredCorrectly"),
                "Liczba poprawnych odpowiedzi na fiszki");

        double avgAnswers = getDouble(stats, "averageAnswersPerSession");
        addRow(table, "Srednia odpowiedzi/sesje",
                String.format(Locale.ROOT, "%.2f", avgAnswers),
                "Srednia liczba odpowiedzi w jednej sesji");

        doc.add(table);
    }

    private Paragraph sectionTitle(String text, PdfFont fontBold) {
        Paragraph p = new Paragraph(text)
                .setFontSize(12)
                .setFontColor(GREEN_DEEP)
                .setMarginTop(4)
                .setMarginBottom(2);
        if (fontBold != null) {
            p.setFont(fontBold);
        } else {
            p.setBold();
        }
        return p;
    }

    private Cell headerCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold().setFontSize(10))
                .setBackgroundColor(GRAY_LIGHT)
                .setBorder(Border.NO_BORDER)
                .setPadding(6);
    }

    private void addRow(Table table, String category, long value, String description) {
        table.addCell(bodyCell(category));
        table.addCell(bodyCell(String.valueOf(value)));
        table.addCell(bodyCell(description));
    }

    private void addRow(Table table, String category, String value, String description) {
        table.addCell(bodyCell(category));
        table.addCell(bodyCell(value));
        table.addCell(bodyCell(description));
    }

    private Cell bodyCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(9))
                .setBorder(Border.NO_BORDER)
                .setPadding(5);
    }

    private void addFooter(Document doc) {
        doc.add(new Paragraph("\n"));
        LineSeparator line = new LineSeparator(new com.itextpdf.kernel.pdf.canvas.draw.SolidLine());
        line.setStrokeColor(GRAY_LIGHT);
        doc.add(line);

        Paragraph footer = new Paragraph("Wygenerowano przez LearnWords Statistics Service")
                .setFontSize(8)
                .setFontColor(GRAY_TEXT)
                .setTextAlignment(TextAlignment.CENTER);

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        Paragraph footerDate = new Paragraph("Data: " + date)
                .setFontSize(8)
                .setFontColor(GRAY_TEXT)
                .setTextAlignment(TextAlignment.CENTER);

        doc.add(footer);
        doc.add(footerDate);
    }

    // === helpery do czytania wartosci z Map ===

    private long getLong(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) return 0L;
        if (v instanceof Number n) return n.longValue();
        return Long.parseLong(v.toString());
    }

    private double getDouble(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) return 0.0;
        if (v instanceof Number n) return n.doubleValue();
        return Double.parseDouble(v.toString());
    }

    /**
     * Pobiera statystyki dla określonego zakresu czasowego
     */
    private Map<String, Object> getStatsForDateRange(String userId, String dateRange) {
        Integer lastDays = getDaysFromDateRange(dateRange);
        
        if (lastDays == null) {
            // "all-time" - pobierz wszystkie statystyki
            return statsService.getUserStats(userId);
        } else {
            // Zakres czasowy - pobierz statystyki z ostatnich N dni
            return statsService.getUserStats(userId, lastDays);
        }
    }

    /**
     * Konwertuje string dateRange na liczbę dni
     */
    private Integer getDaysFromDateRange(String dateRange) {
        return switch (dateRange) {
            case "last-7-days" -> 7;
            case "last-30-days" -> 30;
            case "last-3-months" -> 90;
            case "last-year" -> 365;
            case "all-time" -> null;
            default -> null;
        };
    }

    /**
     * Zwraca czytelną etykietę dla zakresu czasowego
     */
    private String getDateRangeLabel(String dateRange) {
        return switch (dateRange) {
            case "last-7-days" -> "Ostatnie 7 dni";
            case "last-30-days" -> "Ostatnie 30 dni";
            case "last-3-months" -> "Ostatnie 3 miesiace";
            case "last-year" -> "Ostatni rok";
            case "all-time" -> "Caly okres";
            default -> "Caly okres";
        };
    }

    /**
     * Dodaje sekcję z wykresem punktów dziennych
     */
    private void addDailyPointsChartSection(Document doc, String userId, Map<String, Object> stats) throws Exception {
        Map<String, Long> pointsPerDay = statsService.getPointsPerDay(userId);

        if (pointsPerDay.isEmpty()) {
            return;
        }

        Paragraph title = sectionTitle("Punkty w poszczegolnych dniach", null);
        doc.add(title);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        List<Map.Entry<String, Long>> sorted = pointsPerDay.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .limit(30)
                .toList();

        for (Map.Entry<String, Long> entry : sorted) {
            dataset.addValue(entry.getValue(), "Punkty", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                null,
                "Dzien",
                "Punkty",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                false,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        chart.getCategoryPlot().setBackgroundPaint(new Color(249, 250, 251));
        chart.getCategoryPlot().getRenderer().setSeriesPaint(0, new Color(34, 197, 94));
        chart.getCategoryPlot().setOutlineVisible(false);

        int width = 500;
        int height = 200;
        BufferedImage bufferedImage = chart.createBufferedImage(width, height);

        ByteArrayOutputStream imageBaos = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(bufferedImage, "png", imageBaos);
        ImageData imageData = ImageDataFactory.create(imageBaos.toByteArray());
        Image image = new Image(imageData);
        image.setAutoScale(true);
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);

        doc.add(image);
        doc.add(new Paragraph("\n"));
    }
}
