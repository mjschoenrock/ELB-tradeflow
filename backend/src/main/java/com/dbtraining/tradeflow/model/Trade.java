package com.dbtraining.tradeflow.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import org.springframework.cglib.core.Local;

import com.dbtraining.tradeflow.model.ReconResult.Builder;

/**
 * ============================================================================
 * Trade — TICKET-I017 + TICKET-I018 + TICKET-I025 + TICKET-I056
 * ============================================================================
 * WHAT:    Domain object representing a single trade. Central to the system.
 * HOW:     Plain POJO with private final fields and a fluent Builder.
 *          On Day 5 we convert it to a JPA @Entity.
 * WHY:     Immutability + Builder = thread-safe construction + a readable
 *          API at call sites. JPA needs a no-arg constructor — keep it
 *          protected so the Builder is still the only public way in.
 * OBSERVE: Trade t = Trade.builder().tradeRef("TRD-1").quantity(...).build();
 *          Two trades with the same tradeRef should be .equals().
 * ============================================================================
 *  TICKET-I017: define the fields and getters.
 *  TICKET-I018: add the Builder.
 *  TICKET-I025: override equals()/hashCode() using ONLY tradeRef.
 *  TICKET-I056: add JPA annotations — @Entity / @Table / @Id / @ManyToOne.
 * ============================================================================
 *
 * HINTS:
 * - Use BigDecimal for `quantity` + `price` (NEVER double — it loses precision
 *   for money).
 * - Use LocalDate (NOT Date) for tradeDate.
 * - Use Instant (NOT Date) for createdAt.
 * - For JPA: a `protected Trade()` no-arg constructor satisfies Hibernate;
 *   the public path stays via the Builder.
 * - For @ManyToOne on instrument/counterparty: use FetchType.LAZY to avoid
 *   accidental N+1 queries.
 * ============================================================================
 */
public class Trade {

    // ------------------------------------------------------------------------
    // TODO(TICKET-I017): define private final fields:
    // not final as JPA needs to be able to modify them
    private String tradeRef;
    private Long instrumentId;          // or Instrument instrument (Day 5)
    private Long counterpartyId;        // or Counterparty (Day 5)
    private BigDecimal quantity;
    private BigDecimal price;
    private LocalDate tradeDate;
    private TradeStatus status;
    private Instant createdAt;
 
    // ------------------------------------------------------------------------
    // TODO(TICKET-I017 / TICKET-I056): private constructor used by Builder
    //   + protected no-arg constructor for JPA (Day 5).
    // ------------------------------------------------------------------------
    private Trade(Builder builder) {
        this.tradeRef = builder.tradeRef;
        this.instrumentId = builder.instrumentId;
        this.counterpartyId = builder.counterpartyId;
        this.quantity = builder.quantity;
        this.price = builder.price;
        this.tradeDate = builder.tradeDate;
        this.createdAt = builder.createdAt;

        if (builder.status != null){
            this.status = builder.status;
        } else{
            this.status = TradeStatus.PENDING;
        }

        if (builder.createdAt != null){
            this.createdAt = builder.createdAt;
        } else{
            this.createdAt = Instant.now();
        }
    }

    // ------------------------------------------------------------------------
    // TODO(TICKET-I017): public getters (no setters).
    // ------------------------------------------------------------------------
    public String getTradeRef()         { return tradeRef; }
    public Long getInstrumentId()       { return instrumentId; }
    public Long getCounterpartyId()     { return counterpartyId; }
    public BigDecimal getQuantity()     { return quantity; }
    public BigDecimal getPrice()        { return price; }
    public LocalDate getTradeDate()     { return tradeDate; }
    public TradeStatus getStatus()      { return status; }
    public Instant getCreatedAt()       { return createdAt; }

    /** Notional = quantity * price. Computed; not stored. */
    public BigDecimal getNotional() {
        return quantity == null || price == null ? null : quantity.multiply(price);
    }
    // ------------------------------------------------------------------------
    // TODO(TICKET-I025): equals() + hashCode() on tradeRef.
    //   HINT: IntelliJ generate → keep only `tradeRef`.
    // ------------------------------------------------------------------------
    
    @Override
    public boolean equals(Object object){
        if (this == object){
            return true;
        }
        if (!(object instanceof Trade otherTrade)){
            return false;
        }

        return Objects.equals(tradeRef, otherTrade.tradeRef);
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.tradeRef);
    }


    // ------------------------------------------------------------------------
    // TODO(TICKET-I017): toString() formatted for the console list (TICKET-I026)
    //   e.g. "Trade[TRD-1 | SAP.DE | 1000 @ 152.40 EUR | 2026-03-12 | MATCHED]"
    // ------------------------------------------------------------------------
    @Override
    public String toString() {
        return "Trade[" + tradeRef
            + " | instrument=" + instrumentId
            + " | " + quantity + " @ " + price
            + " | " + tradeDate
            + " | " + status + "]";
}
    // ========================================================================
    // TODO(TICKET-I018): fluent Builder.
    //
    //   public static Builder builder() { return new Builder(); }
    //
    //   public static final class Builder {
    //       private String tradeRef;
    //       private BigDecimal quantity;
    //       // ... mirror every field ...
    //
    //       public Builder tradeRef(String v) { this.tradeRef = v; return this; }
    //       public Builder quantity(BigDecimal v) { this.quantity = v; return this; }
    //       // ... setters for every field ...
    //
    //       public Trade build() {
    //           // HINT: validate required fields here.
    //           Objects.requireNonNull(tradeRef, "tradeRef required");
    //           if (quantity == null || quantity.signum() <= 0)
    //               throw new IllegalStateException("quantity must be > 0");
    //           // ...
    //           return new Trade(this);
    //       }
    //   }
    // ========================================================================

    public static Builder builder(){
        return new Builder();
    }

    public static final class Builder{
        private String tradeRef;
        private Long instrumentId;
        private Long counterpartyId;
        private BigDecimal quantity;
        private BigDecimal price;
        private LocalDate tradeDate;
        private TradeStatus status;
        private Instant createdAt;

        public Builder tradeRef(String tradeRef){
            this.tradeRef = tradeRef;
            return this;
        }
        public Builder instrumentId(Long iid){
            this.instrumentId = iid;
            return this;
        }
        public Builder counterpartyId(Long cid){
            this.counterpartyId = cid;
            return this;
        }
        public Builder quantity(BigDecimal quantity){
            this.quantity = quantity;
            return this;
        }
        public Builder price(BigDecimal price){
            this.price = price;
            return this;
        }
        public Builder tradeDate(LocalDate tradeDate){
            this.tradeDate = tradeDate;
            return this;
        }
        public Builder status(TradeStatus status){
            this.status = status;
            return this;
        }
        public Builder createdAt(Instant createdAt){
            this.createdAt = createdAt;
            return this;
        }


        public Trade build(){
            Objects.requireNonNull(tradeRef, "tradeRef input needed");
            Objects.requireNonNull(instrumentId, "instrumentId needed");
            Objects.requireNonNull(counterpartyId, "counterPartyId needed");
            Objects.requireNonNull(quantity, "quantity needed");
            Objects.requireNonNull(price, "price needed");
            Objects.requireNonNull(tradeDate, "tradeDate needed");

            if (quantity.signum() <= 0) throw new IllegalStateException("quantity > 0 needed");

            if (price.signum() < 0 ) throw new IllegalStateException("price > 0 needed");
            return new Trade(this);
        }


    }









}
