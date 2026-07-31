package com.dbtraining.tradeflow.repository;

import com.dbtraining.tradeflow.model.Counterparty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ============================================================================
 * CounterpartyRepository — TICKET-I060 (Day 5 — Spring Data JPA)
 * ============================================================================
 * WHAT:    JPA repository for the Counterparty reference table.
 * WHY:     TradeService needs findById() to validate counterpartyId on
 *          POST /api/v1/trades (TICKET-I062) — inherited from JpaRepository,
 *          no custom finders needed yet.
 * ============================================================================
 */
@Repository
public interface CounterpartyRepository extends JpaRepository<Counterparty, Long> {
}
