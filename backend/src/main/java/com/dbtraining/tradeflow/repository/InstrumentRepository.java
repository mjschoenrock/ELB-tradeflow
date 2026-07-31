package com.dbtraining.tradeflow.repository;

import com.dbtraining.tradeflow.model.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ============================================================================
 * InstrumentRepository — TICKET-I060 (Day 5 — Spring Data JPA)
 * ============================================================================
 * WHAT:    JPA repository for the Instrument reference table.
 * WHY:     TradeService needs findById() to validate instrumentId on
 *          POST /api/v1/trades (TICKET-I062) — inherited from JpaRepository,
 *          no custom finders needed yet.
 * ============================================================================
 */
@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, Long> {
}
