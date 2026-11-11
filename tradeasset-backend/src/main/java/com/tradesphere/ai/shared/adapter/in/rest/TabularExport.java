package com.tradesphere.ai.shared.adapter.in.rest;

import com.tradesphere.ai.shared.domain.error.ValidationException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

/**
 * Server-side CSV and XLSX export (REQ-ANL-004). List endpoints reuse their own filtered query and
 * hand the rows here, so an export always reflects the filters on screen. Every cell is a string
 * already formatted by the server: amounts come from {@code Money.toPlainString()} with the
 * currency in its own column, never as a float.
 */
public final class TabularExport {

    public static final MediaType XLSX =
            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    private static final MediaType CSV = new MediaType("text", "csv", StandardCharsets.UTF_8);

    private TabularExport() {
    }

    /**
     * @param format   csv or xlsx
     * @param baseName file name without extension, for example "letters-of-credit"
     */
    public static ResponseEntity<byte[]> render(String format, String baseName, List<String> headers,
                                                List<List<String>> rows) {
        String normalised = format == null ? "csv" : format.trim().toLowerCase(Locale.ROOT);
        return switch (normalised) {
            case "csv" -> respond(csv(headers, rows), CSV, baseName + ".csv");
            case "xlsx" -> respond(xlsx(headers, rows), XLSX, baseName + ".xlsx");
            default -> throw new ValidationException("format", "format must be csv or xlsx");
        };
    }

    private static ResponseEntity<byte[]> respond(byte[] body, MediaType type, String fileName) {
        return ResponseEntity.ok()
                .contentType(type)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(fileName, StandardCharsets.UTF_8).build().toString())
                .body(body);
    }

    static byte[] csv(List<String> headers, List<List<String>> rows) {
        StringBuilder out = new StringBuilder("\uFEFF"); // BOM so spreadsheet tools read Arabic text as UTF-8
        appendLine(out, headers);
        for (List<String> row : rows) {
            appendLine(out, row);
        }
        return out.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static void appendLine(StringBuilder out, List<String> cells) {
        for (int i = 0; i < cells.size(); i++) {
            if (i > 0) {
                out.append(',');
            }
            out.append(escape(cells.get(i)));
        }
        out.append("\r\n");
    }

    /** Quotes when needed and neutralises spreadsheet formula injection. */
    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        String safe = value;
        if (!safe.isEmpty() && "=+-@\t\r".indexOf(safe.charAt(0)) >= 0 && !safe.matches("-?\\d+(\\.\\d+)?")) {
            safe = "'" + safe;
        }
        if (safe.contains(",") || safe.contains("\"") || safe.contains("\n") || safe.contains("\r")) {
            safe = "\"" + safe.replace("\"", "\"\"") + "\"";
        }
        return safe;
    }

    static byte[] xlsx(List<String> headers, List<List<String>> rows) {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(200); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Export");
            writeRow(sheet.createRow(0), headers);
            int index = 1;
            for (List<String> row : rows) {
                writeRow(sheet.createRow(index++), row);
            }
            workbook.write(out);
            workbook.dispose();
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void writeRow(Row row, List<String> cells) {
        for (int i = 0; i < cells.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(cells.get(i) == null ? "" : cells.get(i));
        }
    }
}
