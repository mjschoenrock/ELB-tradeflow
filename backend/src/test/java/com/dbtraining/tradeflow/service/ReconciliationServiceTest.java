package com.dbtraining.tradeflow.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dbtraining.tradeflow.dto.Discrepancy;
import com.dbtraining.tradeflow.dto.ReconReport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;


// mockito imports
import com.dbtraining.tradeflow.model.*;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private final ReconciliationService service = new ReconciliationService();

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
        BaseTrade in  = equityWith("TRD-001", new BigDecimal("100"), new BigDecimal("245.50"), LocalDate.of(2026, 3, 1));
        BaseTrade out = equityWith("TRD-001", new BigDecimal("100"), new BigDecimal("245.99"), LocalDate.of(2026, 3, 1));

        ReconReport report = service.matchTrades(List.of(in), List.of(out));

        assertThat(report.matched()).isEmpty();
        assertThat(report.discrepancies()).hasSize(1);
        Discrepancy d = report.discrepancies().get(0);
        assertThat(d.tradeRef()).isEqualTo("TRD-001");
        assertThat(d.types()).containsExactly(DiscrepancyType.PRICE_MISMATCH);
}

private static BaseTrade equityWith(String ref, BigDecimal qty, BigDecimal price, LocalDate date) {
    return EquityTrade.builder()
            .tradeRef(ref).instrumentId(1L).counterpartyId(1L)
            .quantity(qty).price(price).tradeDate(date)
            .status(TradeStatus.MATCHED).exchange("XETRA").lotSize(100)
            .build();
}

    // TODO(TICKET-I050): test matchTrades_missingExternal_flagsMissingTrade.
    @Test
    void matchTrades_missingExternal_flagsMissingTrade() {
        List<BaseTrade> internal = List.of(equity("TRD-INT-ONLY"));
        List<BaseTrade> external = List.of();

        ReconReport report = service.matchTrades(internal, external);

        assertThat(report.discrepancies()).hasSize(1);
        assertThat(report.discrepancies().get(0).tradeRef()).isEqualTo("TRD-INT-ONLY");
        assertThat(report.discrepancies().get(0).types())
                .containsExactly(DiscrepancyType.MISSING_TRADE);
    }

    @Test
    void matchTrades_missingInternal_flagsMissingTrade() {
        List<BaseTrade> internal = List.of();
        List<BaseTrade> external = List.of(equity("TRD-EXT-ONLY"));

        ReconReport report = service.matchTrades(internal, external);

        assertThat(report.discrepancies()).hasSize(1);
        assertThat(report.discrepancies().get(0).tradeRef()).isEqualTo("TRD-EXT-ONLY");
        assertThat(report.discrepancies().get(0).types())
                .containsExactly(DiscrepancyType.MISSING_TRADE);
    }



    // TODO(TICKET-I051): test with @Mock TradeDAO + verify(...).findAll() called.
    @Test
    void mockedTradeDAO_findAllCalledOnce() {
        fail("TICKET-I051: implement test");
    }

    // TODO(TICKET-I052): test with @Mock ReconResultDAO + ArgumentCaptor.
    @Test
    void runForAll_oneDiscrepancy_insertsOneReconResult() {
        Trade internalOnly = sampleTrade("TRD-INT-ONLY");
        when(tradeDAO.findAll()).thenReturn(List.of(internalOnly));

        service.runForAll();

        ArgumentCaptor<ReconResult> captor = ArgumentCaptor.forClass(ReconResult.class);
        verify(reconResultDAO, times(1)).insert(captor.capture());
        ReconResult inserted = captor.getValue();

        assertThat(inserted.getDiscrepancyType())
                .isEqualTo(DiscrepancyType.MISSING_TRADE);
        assertThat(inserted.getStatus())
                .isEqualTo(ReconResult.Status.OPEN);
    }

    @Test
    void runForAll_allMatched_neverCallsInsert() {
        Trade matched = sampleTrade("TRD-1");
        when(tradeDAO.findAll()).thenReturn(List.of(matched));

        service.runForAll();

        verify(reconResultDAO, never()).insert(any(ReconResult.class));
    }
}
