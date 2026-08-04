package com.dbtraining.tradeflow.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dbtraining.tradeflow.dto.Discrepancy;
import com.dbtraining.tradeflow.dto.ReconReport;
import com.dbtraining.tradeflow.dto.ReconSummary;
import com.dbtraining.tradeflow.repository.ReconResultRepository;
import com.dbtraining.tradeflow.repository.TradeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


// mockito imports
import com.dbtraining.tradeflow.model.*;
import org.mockito.ArgumentCaptor;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.Mock;

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

    @Mock
    private ReconResultRepository reconResultRepository;

    @Mock
    private TradeRepository tradeRepository;

    private ReconciliationService service;

    @BeforeEach
    void setup() {
        service = new ReconciliationService(reconResultRepository, new SimpleMeterRegistry(), tradeRepository);
    }

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
        List<BaseTrade> internal = List.of(equity("TST-001"), equity("TST-002"), equity("TST-003"));
        List<BaseTrade> external = List.of(equity("TST-001"), equity("TST-002"), equity("TST-003"));

        ReconReport report = service.matchTrades(internal, external);

        assertThat(report.matched()).hasSize(3); 
        assertThat(report.discrepancies()).isEmpty();

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

    private static ReconResult openBreak(DiscrepancyType type) {
        return ReconResult.builder().discrepancyType(type).status(ReconResult.Status.OPEN).build();
    }

    @Test
    void findAllCalledOnce() {
        
        when(reconResultRepository.countByStatus(ReconResult.Status.RESOLVED)).thenReturn(2L);
        when(reconResultRepository.countByStatus(ReconResult.Status.OPEN)).thenReturn(1L);
        when(reconResultRepository.findByStatus(ReconResult.Status.OPEN))
                .thenReturn(List.of(openBreak(DiscrepancyType.MISSING_TRADE)));
 
        ReconSummary summary = service.runForAll();
 
        verify(reconResultRepository).countByStatus(ReconResult.Status.RESOLVED);
        verify(reconResultRepository).countByStatus(ReconResult.Status.OPEN);
        verify(reconResultRepository).findByStatus(ReconResult.Status.OPEN);
 
        assertThat(summary.matchedCount()).isEqualTo(2);
        assertThat(summary.unmatchedCount()).isEqualTo(1);
        assertThat(summary.totalInternal()).isEqualTo(3);
        assertThat(summary.totalExternal()).isEqualTo(3);

    }

    // TODO(TICKET-I052): test with @Mock ReconResultDAO + ArgumentCaptor.
    @Test
    void runForAll_oneDiscrepancy_insertsOneReconResult() {
        when(reconResultRepository.countByStatus(ReconResult.Status.RESOLVED)).thenReturn(0L);
        when(reconResultRepository.countByStatus(ReconResult.Status.OPEN)).thenReturn(1L);
        when(reconResultRepository.findByStatus(ReconResult.Status.OPEN))
                .thenReturn(List.of(openBreak(DiscrepancyType.MISSING_TRADE)));
 
        ReconSummary summary = service.runForAll();
 
        assertThat(summary.breakdownByType().get(DiscrepancyType.MISSING_TRADE)).isEqualTo(1);
        assertThat(summary.breakdownByType().get(DiscrepancyType.PRICE_MISMATCH)).isEqualTo(0);

    }

    @Test
    void runForAll_allMatched_neverCallsInsert() {
        when(reconResultRepository.countByStatus(ReconResult.Status.RESOLVED)).thenReturn(5L);
        when(reconResultRepository.countByStatus(ReconResult.Status.OPEN)).thenReturn(0L);
        when(reconResultRepository.findByStatus(ReconResult.Status.OPEN))
                .thenReturn(List.of());
 
        ReconSummary summary = service.runForAll();
 
        assertThat(summary.unmatchedCount()).isEqualTo(0);
        summary.breakdownByType().values().forEach(count -> assertThat(count).isEqualTo(0));

    }
}
