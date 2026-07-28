package com.dbtraining.tradeflow.model;
import com.dbtraining.tradeflow.model.ReconResult.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import org.springframework.cglib.core.Local;

import com.dbtraining.tradeflow.model.ReconResult.Builder;
/**
 * ============================================================================
 * EquityTrade — TICKET-I029
 * ============================================================================
 * WHAT:    A trade on an equity (shares of a listed company).
 * WHY:     Equities are exchange-listed, so we need the exchange name + lot size.
 * ============================================================================
 *  TODO(TICKET-I029):
 *    extends BaseTrade.
 *    Extra fields:
 *      private final String exchange;     // e.g. "XETRA", "NASDAQ"
 *      private final int lotSize;         // typical 100 for US, 1 for European
 *
 *    assetClassDescription() returns "Equity on " + exchange.
 * ============================================================================
 */
public class EquityTrade extends BaseTrade {
    // TODO(TICKET-I029): extend BaseTrade, add exchange + lotSize, override
    //                    assetClassDescription().

    private String exchange;
    private int lotSize;

    protected EquityTrade(){
        continue;
    }

    @Override
    public String assetClassDescription() {
        return "Equity Trade"; 
    }



}
