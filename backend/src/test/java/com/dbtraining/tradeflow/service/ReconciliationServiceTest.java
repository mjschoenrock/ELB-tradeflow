package com.dbtraining.tradeflow.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.dbtraining.tradeflow.model.BaseTrade;
import com.dbtraining.tradeflow.model.EquityTrade;
import com.dbtraining.tradeflow.model.TradeStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * ============================================================================
 * ReconciliationServiceTest — TICKET-I048..I053
 * ============================================================================
 * WHAT:    JUnit + Mockito tests for the recon engine.
 * HOW:     @ExtendWith(MockitoExtension.class). Mock the DAOs, build sample
 *          trade lists, assert on the returned ReconReport.
 * WHY:     Day 4 sets a 70% coverage target. ReconciliationService is the
 *          critical path — it gets the most attention.
 * OBSERVE: `mvn test` runs these in a few seconds; JaCoCo report shows the
 *          coverage % per class.
 * ============================================================================
 */
@ExtendWith(MockitoExtension.class)
class ReconciliationServiceTest {

    private static BaseTrade equity(String tradeRef) {
        return EquityTrade.builder()
                .tradeRef(tradeRef).instrumentId(1L).counterpartyId(1L)
                .quantity(new BigDecimal("100")).price(new BigDecimal("245.50"))
                .tradeDate(LocalDate.of(2026, 3, 1))
                .status(TradeStatus.MATCHED)
                .exchange("XETRA").lotSize(100)
                .build();
    }

    // TODO(TICKET-I048): test matchTrades_allMatched_returnsEmptyDiscrepancies.
    @Test
    void matchTrades_allMatched_returnsEmptyDiscrepancies() {
        fail("TICKET-I048: implement test");
        List<BaseTrade> internal = List.of(equity("TST-001"), equity("TST-002"), equity("TST-003"));
        List<BaseTrade> external = List.of(equity("TST-001"), equity("TST-002"), equity("TST-003"));

        ReconReport report = service.matchTrades(internal, external);

        assertThat(report.matched()).hasSize(3); 



    }

    // TODO(TICKET-I049): test matchTrades_priceMismatch_flagsDiscrepancy.
    @Test
    void matchTrades_priceMismatch_flagsDiscrepancy() {
        fail("TICKET-I049: implement test");
    }

    // TODO(TICKET-I050): test matchTrades_missingExternal_flagsMissingTrade.
    @Test
    void matchTrades_missingExternal_flagsMissingTrade() {
        List<BaseTrade> internal = List.of(equity)
    }

    // TODO(TICKET-I051): test with @Mock TradeDAO + verify(...).findAll() called.
    @Test
    void mockedTradeDAO_findAllCalledOnce() {
        fail("TICKET-I051: implement test");
    }

    // TODO(TICKET-I052): test with @Mock ReconResultDAO + ArgumentCaptor.
    @Test
    void mockedReconResultDAO_insertCalledPerDiscrepancy() {
        fail("TICKET-I052: implement test");
    }
}
