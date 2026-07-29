package com.dbtraining.tradeflow.controller;

import com.dbtraining.tradeflow.dto.TradeDto;
import com.dbtraining.tradeflow.dto.TradeRequest;
import com.dbtraining.tradeflow.service.TradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.net.URI;

/**
 * ============================================================================
 * TradeController — TICKET-I068 + I069 + I070 + I071
 * ============================================================================
 * WHAT:    REST controller for /api/v1/trades.
 * HOW:     @RestController + @RequestMapping.
 * WHY:     Single entry-point for trade CRUD from the React UI and Postman.
 *          Stays thin: parse + validate + delegate to TradeService.
 * OBSERVE: Day-0 — every endpoint returns empty or 501-style throw. As
 *          students complete Day-1..6 tickets, the controller wires through
 *          to the real DB and the React UI populates.
 * ============================================================================
 *
 *  TICKET-I068: GET    /api/v1/trades (paginated + filterable)
 *  TICKET-I069: POST   /api/v1/trades (@Valid + 201 Created)
 *  TICKET-I070: PUT    /api/v1/trades/{id}/status
 *  TICKET-I071: DELETE /api/v1/trades/{id}  (soft delete)
 *  TICKET-I064: OpenAPI annotations on every method
 * ============================================================================
 */
@RestController
@RequestMapping("/api/v1/trades")
@Tag(name = "Trades", description = "Trade management endpoints")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    // ------------------------------------------------------------------------
    // TICKET-I068
    // ------------------------------------------------------------------------
        @Operation(summary = "List trades")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trades returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter values supplied")
        })
    @GetMapping
    public List<TradeDto> list(
            @Parameter(description = "Optional trade status filter")
            @RequestParam(required = false) String status,
            @Parameter(description = "Optional inclusive start date filter")
            @RequestParam(required = false) LocalDate from,
            @Parameter(description = "Optional inclusive end date filter")
            @RequestParam(required = false) LocalDate to
    ) {
        // TODO(TICKET-I068): replace this empty response with a real DB-backed
        //   call once the JDBC DAO (Day 4 / TICKET-I045) or JPA repository
        //   (Day 5 / TICKET-I060+I062) is in place.
        //   For Day 1, returning an empty list keeps the React UI booting
        //   gracefully (shows "no trades match") while you build the schema.
        return Collections.emptyList();
    }

    // ------------------------------------------------------------------------
    // TICKET-I069
    // ------------------------------------------------------------------------
    @Operation(summary = "Create a new trade")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trade created successfully"),
            @ApiResponse(responseCode = "400", description = "Request body validation failed")
    })
    @PostMapping
    public ResponseEntity<TradeDto> create(@Valid @RequestBody TradeRequest request) {
        // TODO(TICKET-I069): call service, build Location header, return 201.
        TradeDto saved = tradeService.createTrade(request);
        return ResponseEntity.created(URI.create("/api/v1/trades/" + saved.id())).body(saved);
    }

    // ------------------------------------------------------------------------
    // TICKET-I070
    // ------------------------------------------------------------------------
        @Operation(summary = "Update a trade's status")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trade status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status supplied"),
            @ApiResponse(responseCode = "404", description = "Trade not found")
        })
    @PutMapping("/{id}/status")
        public TradeDto updateStatus(
            @Parameter(description = "Trade identifier")
            @PathVariable Long id,
            @Parameter(description = "New trade status payload")
            @RequestBody StatusUpdate body) {
        // TODO(TICKET-I070): delegate to tradeService.updateStatus(id, body.status()).
        throw new UnsupportedOperationException("TICKET-I070");
    }

    // ------------------------------------------------------------------------
    // TICKET-I071 — soft delete
    // ------------------------------------------------------------------------
        @Operation(summary = "Soft-delete a trade")
        @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Trade soft-deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Trade not found")
        })
    @DeleteMapping("/{id}")
        public ResponseEntity<Void> softDelete(
            @Parameter(description = "Trade identifier")
            @PathVariable Long id) {
        // TODO(TICKET-I071): tradeService.softDelete(id); return 204.
        throw new UnsupportedOperationException("TICKET-I071");
    }

    /** Tiny inbound record for PUT /{id}/status. */
    public record StatusUpdate(String status) {}
}
