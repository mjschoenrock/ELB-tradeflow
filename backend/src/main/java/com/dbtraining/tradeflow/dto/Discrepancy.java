package com.dbtraining.tradeflow.dto;

import com.dbtraining.tradeflow.model.DiscrepancyType;

import java.util.List;

/**
 * Discrepancy — supporting DTO for TICKET-I034 / TICKET-I035.
 *
 * One tradeRef can carry several mismatched fields at once (e.g. both
 * price and quantity differ), so ReconciliationService.matchTrades emits
 * a list of DiscrepancyType per Discrepancy rather than one row per type.
 */
public record Discrepancy(String tradeRef, List<DiscrepancyType> types) {
}
