package com.dbtraining.tradeflow.dto;

import com.dbtraining.tradeflow.model.BaseTrade;

import java.util.List;

/**
 * ReconReport — supporting DTO for TICKET-I034.
 *
 * Raw output of ReconciliationService.matchTrades: the input list sizes,
 * the list of cleanly-matched internal trades, and the list of per-trade
 * discrepancies. TICKET-I036 reduces this into the rolled-up ReconSummary.
 */
public record ReconReport(
        int totalInternal,
        int totalExternal,
        List<BaseTrade> matched,
        List<Discrepancy> discrepancies
) {
}
