package com.dbtraining.tradeflow.controller;

import com.dbtraining.tradeflow.dto.ReconResultDto;
import com.dbtraining.tradeflow.dto.ReconSummary;
import com.dbtraining.tradeflow.model.ReconResult;
import com.dbtraining.tradeflow.service.ReconciliationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================
 * ReconController — TICKET-I072 + I073 + I074
 * ============================================================================
 * WHAT:    REST controller for /api/v1/recon.
 * ============================================================================
 *  TICKET-I072: POST /api/v1/recon/run         -> ReconSummary
 *  TICKET-I073: GET  /api/v1/recon/results     -> Page<ReconBreakDto>
 *  TICKET-I074: PUT  /api/v1/recon/{id}/resolve -> 204
 * ============================================================================
 *  Day-0 stub: GET returns empty list so the React Recon page boots clean.
 *  Replace with real DB-backed reads once the schema exists (Day 1) and
 *  the JDBC/JPA layer is in place (Day 4 / Day 5).
 * ============================================================================
 */
@RestController
@RequestMapping("/api/v1/recon")
@Tag(name = "Reconciliation", description = "Run recon + manage breaks")
public class ReconController {

    private final ReconciliationService reconService;
    public ReconController(ReconciliationService reconService) {
        this.reconService = reconService;
    }

    @Operation(summary = "Trigger a reconciliation run")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reconciliation summary returned successfully")
        })
    @PostMapping("/run")
    public ReconSummary run() {
        // TODO(TICKET-I072): inject ReconciliationService, call run(), return summary.
        throw new UnsupportedOperationException("TICKET-I072");
    }

    @Operation(summary = "List reconciliation results")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reconciliation results returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status filter supplied")
        })
    @Operation(summary = "List recon breaks (paginated; defaults to OPEN)")
    @GetMapping("/results")
    public Page<ReconResultDto> listResults(
            @RequestParam(required = false, defaultValue = "OPEN") String status,
            @RequestParam(required = false) Long counterpartyId,
            @PageableDefault(size = 20) Pageable pageable) {
        ReconResult.Status parsed = ReconResult.Status.valueOf(status.toUpperCase());
        return reconService.listBreaks(parsed, counterpartyId, pageable);
    }

    @Operation(summary = "Mark a break as resolved")
        @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reconciliation break resolved successfully"),
            @ApiResponse(responseCode = "404", description = "Reconciliation break not found")
        })
    @PutMapping("/{id}/resolve")
        public void resolve(
            @Parameter(description = "Reconciliation break identifier")
            @PathVariable Long id) {
        // TODO(TICKET-I074): update status to RESOLVED, set resolved_at, write audit log.
        throw new UnsupportedOperationException("TICKET-I074");
    }
}
