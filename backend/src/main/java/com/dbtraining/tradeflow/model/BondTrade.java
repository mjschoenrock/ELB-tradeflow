package com.dbtraining.tradeflow.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ============================================================================
 * BondTrade — TICKET-I031
 * ============================================================================
 * WHAT:    A trade on a fixed-income instrument.
 * WHY:     Bonds need coupon and maturity to compute yield / settlement.
 * ============================================================================
 *  TODO(TICKET-I031):
 *    extends BaseTrade.
 *    Extra fields:
 *      private final BigDecimal couponRate;       // 0.0 .. 100.0
 *      private final LocalDate maturityDate;      // must be after tradeDate
 *      private final BigDecimal faceValue;
 *
 *    Validate in builder:
 *      - couponRate >= 0 && couponRate <= 100
 *      - maturityDate.isAfter(tradeDate)
 *
 *    assetClassDescription() returns
 *      "Bond coupon " + couponRate + "% mat " + maturityDate.
 * ============================================================================
 */
public class BondTrade extends BaseTrade {
    // TODO(TICKET-I031): extend BaseTrade, add coupon/maturity/faceValue, override.
    private final BigDecimal couponRate;
    private final LocalDate maturityDate;
    private final BigDecimal faceValue;

    private BondTrade(Builder b) {
        super(b.tradeRef, b.instrumentId, b.counterpartyId, b.quantity, b.price, b.tradeDate, b.status, b.createdAt);

        this.couponRate = b.couponRate;
        this.maturityDate = b.maturityDate;
        this.faceValue = b.faceValue;

        if(couponRate.signum() < 0 || couponRate.compareTo(BigDecimal.valueOf(100))) {
            throw new IllegalStateException("coupon rate should be between 0 and 100");
        }
        
        if (!(maturityDate.isAfter(tradeDate))) {
            throw new IllegalStateException("maturityDate must be after tradeDate");
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String assetClassDescription() {
        return "Bound coupon " + couponRate + "% mat " + maturityDate;
}

    public static final class Builder {
        private String tradeRef;
        private Long instrumentId;
        private Long counterpartyId;
        private BigDecimal quantity;
        private BigDecimal price;
        private LocalDate tradeDate;
        private TradeStatus status;
        private Instant createdAt;
        private BigDecimal couponRate;
        private LocalDate maturityDate;
        private int BigDecimal faceValue;

        public Builder getTradeRef(String tradeRef){
            this.tradeRef = tradeRef;
            return this;
        }
        public Builder getInstrumentId(Long instrumentId){
            this.instrumentId = instrumentId;
            return this;
        }
        public Builder counterpartyId(Long counterPartyId){ 
            this.counterpartyId = counterPartyId; return this; 
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
        public Builder couponRate(BigDecimal couponRate){ 
            this.couponRate = couponRate;       
            return this; 
        }
        public Builder maturityDate(LocalDate maturityDate){
            this.maturityDate = maturityDate;        
            return this; 
        }
        public Builder faceValue(BigDecimal faceValue){
            this.faceValue = faceValue;        
            return this; 
        }

        public BondTrade build() {
            return new BondTrade(this);
        }

    }
   
}
