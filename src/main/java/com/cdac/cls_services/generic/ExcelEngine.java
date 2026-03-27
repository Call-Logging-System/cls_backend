package com.cdac.cls_services.generic;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Function;

/**
 * Generic Excel export engine.
 * Usage:
 * <pre>{@code
 *   List<ExcelColumn<MyDto>> cols = List.of(
 *       ExcelColumn.<MyDto>builder().header("Name").width(25).value(MyDto::getName).build(),
 *       ExcelColumn.<MyDto>builder().header("Date").width(13).type(DATE).value(MyDto::getDate).build()
 *   );
 *   byte[] xlsx = excelExportService.export(rows, cols, "Sheet1");
 * }</pre>
 */
@Service
public class ExcelEngine {

    /**
     * Builds an .xlsx workbook from {@code data} using the supplied column descriptors.
     *
     * @param data      the rows to render
     * @param columns   ordered list of column descriptors
     * @param sheetName name for the Excel sheet tab
     * @return raw bytes of the generated .xlsx file
     */
    /** Single-sheet export. */
    public <T> byte[] export(List<T> data, List<ExcelColumn<T>> columns, String sheetName) {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            writeSheet(wb, sheetName, data, columns);
            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Excel export failed: " + e.getMessage(), e);
        }
    }

    /**
     * Multi-sheet export — one tab per month, ordered chronologically.
     *
     * @param data          all rows
     * @param columns       column descriptors
     * @param dateExtractor function to get LocalDate from a row (used for grouping)
     */
    public <T> byte[] exportByMonth(List<T> data,
                                    List<ExcelColumn<T>> columns,
                                    Function<T, LocalDate> dateExtractor) {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Group rows by sheet name (e.g. "Feb26"), preserving chronological order
            Map<String, List<T>> grouped = new LinkedHashMap<>();
            for (T item : data) {
                LocalDate date = dateExtractor.apply(item);
                String sheetName = date != null ? formatSheetName(date) : "Unknown";
                grouped.computeIfAbsent(sheetName, k -> new ArrayList<>()).add(item);
            }

            for (Map.Entry<String, List<T>> entry : grouped.entrySet()) {
                writeSheet(wb, entry.getKey(), entry.getValue(), columns);
            }

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Excel export failed: " + e.getMessage(), e);
        }
    }

    // ── Core sheet writer ────────────────────────────────────────────────────

    private <T> void writeSheet(XSSFWorkbook wb, String sheetName,
                                List<T> data, List<ExcelColumn<T>> columns) {

        Sheet sheet = wb.createSheet(sheetName);
        CreationHelper ch = wb.getCreationHelper();

        short dateFormat = ch.createDataFormat().getFormat("mm-dd-yy");
        short timeFormat = ch.createDataFormat().getFormat("h:mm");

        CellStyle headerStyle  = buildHeaderStyle(wb);
        CellStyle centerStyle  = buildDataStyle(wb, "Cambria", (short) 12, HorizontalAlignment.CENTER, null);
        CellStyle noAlignStyle = buildDataStyle(wb, "Calibri", (short) 11, null,                        null);  // col B
        CellStyle leftStyle    = buildDataStyle(wb, "Cambria", (short) 12, HorizontalAlignment.LEFT,   null);  // col D
        CellStyle dateStyle    = buildDataStyle(wb, "Cambria", (short) 12, HorizontalAlignment.CENTER, dateFormat);
        CellStyle timeStyle    = buildDataStyle(wb, "Cambria", (short) 12, HorizontalAlignment.CENTER, timeFormat);

        // Header row
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(110.25f);

        for (int i = 0; i < columns.size(); i++) {
            ExcelColumn<T> col = columns.get(i);
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(col.getHeader());
            cell.setCellStyle(headerStyle);
            double w = (i == 1) ? 22.7109375 : col.getWidthInChars();
            sheet.setColumnWidth(i, (int)(w * 256));
        }

        // Data rows
        int rowNum = 1;
        for (T item : data) {
            Row row = sheet.createRow(rowNum++);
            row.setHeightInPoints(63f);

            for (int i = 0; i < columns.size(); i++) {
                ExcelColumn<T> col = columns.get(i);
                Object value = col.getValueExtractor().apply(item);
                Cell cell = row.createCell(i);

                switch (col.getCellType()) {

                    case DATE -> {
                        if (value instanceof LocalDate ld) {
                            cell.setCellValue(ld);
                            cell.setCellStyle(dateStyle);
                        } else {
                            cell.setCellValue(value != null ? value.toString() : "");
                            cell.setCellStyle(centerStyle);
                        }
                    }

                    case TIME -> {
                        if (value instanceof LocalTime lt) {
                            cell.setCellValue(lt.toSecondOfDay() / 86400.0);
                            cell.setCellStyle(timeStyle);
                        } else {
                            cell.setCellValue(value != null ? value.toString() : "");
                            cell.setCellStyle(centerStyle);
                        }
                    }

                    default -> {
                        cell.setCellValue(value != null ? value.toString() : "");
                        if (i == 1) {
                            cell.setCellStyle(noAlignStyle);
                        } else if (i == 3) {
                            cell.setCellStyle(leftStyle);
                        } else {
                            cell.setCellStyle(centerStyle);
                        }
                    }
                }
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /** 2026-02-15 → "Feb26",  2025-10-01 → "Oct25" */
    private String formatSheetName(LocalDate date) {
        String month = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        String year  = String.valueOf(date.getYear()).substring(2);
        return month + year;
    }

    // ── Style builders ───────────────────────────────────────────────────────

    private CellStyle buildHeaderStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();

        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setFontName("Cambria");
        style.setFont(font);

        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);

        applyThinBorders(style);
        return style;
    }

    private CellStyle buildDataStyle(XSSFWorkbook wb, String fontName, short fontSize,
                                     HorizontalAlignment hAlign, Short dataFormat) {
        CellStyle style = wb.createCellStyle();

        Font font = wb.createFont();
        font.setFontHeightInPoints(fontSize);
        font.setFontName(fontName);
        style.setFont(font);

        if (hAlign != null) {
            style.setAlignment(hAlign);
        }
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);

        if (dataFormat != null) {
            style.setDataFormat(dataFormat);
        }

        applyThinBorders(style);
        return style;
    }

    private void applyThinBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }
}
