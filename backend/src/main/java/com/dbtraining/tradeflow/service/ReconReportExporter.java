package com.dbtraining.tradeflow.service;

import com.dbtraining.tradeflow.model.ReconResult;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * ============================================================================
 * ReconReportExporter — TICKET-I037
 * ============================================================================
 * WHAT:   Writes reconciliation results to a CSV file.
 * HOW:     Vanilla java.nio.file + BufferedWriter. NO external CSV library
 *          — the point is to learn quoting/escaping by hand.
 * WHY:     Ops users download daily recon CSVs to feed downstream systems.
 * OBSERVE: Field values containing `,` or `"` are quoted; embedded `"` is
 *          doubled to `""`.
 * ============================================================================
 */
public class ReconReportExporter {

    private static final String HEADER = "trade_id,status,discrepancy_type,resolved_at";

    public void exportReconReport(List<ReconResult> results, Path target) throws IOException {
        if (target == null) {
            throw new IllegalArgumentException("Target path cannot be null");
        }

        // 1. Create temporary file in the same directory for atomic move safety
        Path parentDir = target.getParent();
        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
        
        Path tempFile = Files.createTempFile(
            parentDir, 
            target.getFileName().toString() + "-", 
            ".tmp"
        );

        try {
            // 2. Write CSV data using try-with-resources
            try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
                // Write Header
                writer.write(HEADER);
                writer.newLine();

                // Write Rows
                if (results != null) {
                    for (ReconResult result : results) {
                        String row = String.join(",",
                            escape(result.getTradeId()),
                            escape(result.getStatus()),
                            escape(result.getDiscrepancyType()),
                            escape(result.getResolvedAt())
                        );
                        writer.write(row);
                        writer.newLine();
                    }
                }
            }

            // 3. Move temporary file to target location atomically
            Files.move(
                tempFile, 
                target, 
                StandardCopyOption.ATOMIC_MOVE, 
                StandardCopyOption.REPLACE_EXISTING
            );

        } catch (Exception e) {
            // Clean up temporary file if writing or moving fails
            Files.deleteIfExists(tempFile);
            throw e;
        }
    }

    /**
     * Escapes values according to RFC 4180 CSV standard:
     * - Null values are returned as empty strings.
     * - Fields with commas, double quotes, or newlines are wrapped in double quotes.
     * - Internal double quotes are escaped by doubling them (" -> "").
     */
    private String escape(Object rawValue) {
        if (rawValue == null) {
            return "";
        }

        String value = rawValue.toString();

        // Check if value needs quoting (contains comma, quote, or line breaks)
        boolean needsQuoting = value.contains(",") || 
                               value.contains("\"") || 
                               value.contains("\n") || 
                               value.contains("\r");

        if (needsQuoting) {
            // Double up internal quotes
            value = value.replace("\"", "\"\"");
            // Enclose in quotes
            return "\"" + value + "\"";
        }

        return value;
    }
}