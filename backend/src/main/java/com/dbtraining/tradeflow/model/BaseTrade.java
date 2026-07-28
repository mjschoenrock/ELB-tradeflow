package com.dbtraining.tradeflow.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * ============================================================================
 * BaseTrade — TICKET-I028
 * ============================================================================
 * WHAT:    Abstract superclass for asset-class-specific trade types
 *          (EquityTrade, FXTrade, BondTrade).
 * HOW:     Holds the common fields. Concrete classes add asset-class-specific
 *          fields and override the abstract describer.
 * WHY:     Demonstrates inheritance + polymorphism in real domain terms,
 *          and gives `ReconciliationService` one type to operate on.
 * OBSERVE: You CANNOT do `new BaseTrade(...)` — only the subclasses.
 *
 * GOTCHA:  Read "Effective Java" Item 18 — favour composition over inheritance.
 *          Discuss with your team: is BaseTrade the right call, or would a
 *          single Trade with an AssetClass enum + composition (e.g.
 *          AssetSpecificDetails) be cleaner? Document your choice in the PR.
 * ============================================================================
 */
public abstract class BaseTrade {

    // TODO(TICKET-I028): protected final fields for the shared values
    //   tradeRef, instrumentId, counterpartyId, quantity, price, tradeDate,
    //   status, createdAt.

    // TODO(TICKET-I028): protected constructor (subclasses call super(...)).
    protected final String tradeRef;
    protected final Long instrumentId;
    protected final Long counterpartyId;
    protected final BigDecimal quantity;
    protected final BigDecimal price;
    protected final LocalDate tradeDate;
    protected final TradeStatus status;
    protected final Instant createdAt;


    protected BaseTrade(String tradeRef, Long instrumentId, Long counterpartyId, BigDecimal quantity, BigDecimal price, LocalDate tradeDate, TradeStatus status, Instant createdAt){
            this.tradeRef = Objects.requireNonNull(tradeRef, "tradeRef input needed");
            this.instrumentId = Objects.requireNonNull(instrumentId, "instrumentId needed");
            this.counterpartyId= Objects.requireNonNull(counterpartyId, "counterPartyId needed");
            this.quantity = Objects.requireNonNull(quantity, "quantity needed");
            this.price = Objects.requireNonNull(price, "price needed");
            this.tradeDate = Objects.requireNonNull(tradeDate, "tradeDate needed");

            if (quantity.signum() <= 0) throw new IllegalStateException("quantity > 0 needed");

            if (price.signum() < 0 ) throw new IllegalStateException("price > 0 needed");
            if (status != null) {
                this.status = status;
            } else {
                this.status = TradeStatus.PENDING;
            }

            if (createdAt != null) {
                this.createdAt = createdAt;
            } else {
                this.createdAt = Instant.now();
            }
    }



    // TODO(TICKET-I028): public getters.
    
    public String getTradeRef()       { return tradeRef; }
    public Long getInstrumentId()     { return instrumentId; }
    public Long getCounterpartyId()   { return counterpartyId; }
    public BigDecimal getQuantity()   { return quantity; }
    public BigDecimal getPrice()      { return price; }
    public LocalDate getTradeDate()   { return tradeDate; }
    public TradeStatus getStatus()    { return status; }
    public Instant getCreatedAt()     { return createdAt; }
    
    /**
     * Each asset class returns its own description for logs/UI.
     * EquityTrade → "Equity on XETRA"
     * FXTrade     → "FX EUR/USD"
     * BondTrade   → "Bond coupon 4.50% mat 2030-06-15"
     */
    public abstract String assetClassDescription();

    @Override
    public boolean equals(Object object){
        if (this == object){
            return true;
        }
        if (!(object instanceof BaseTrade otherTrade)){
            return false;
        }

        return Objects.equals(tradeRef, otherTrade.tradeRef);
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.tradeRef);
    }

    @Override
    public String toString() {
        return "Trade[" + tradeRef
            + " | instrument=" + instrumentId
            + " | " + quantity + " @ " + price
            + " | " + tradeDate
            + " | " + status + "]";
    }

    public BigDecimal getNotional() {
        return quantity == null || price == null ? null : quantity.multiply(price);
    }






    
}
