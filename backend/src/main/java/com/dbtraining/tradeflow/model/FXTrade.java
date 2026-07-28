

package com.dbtraining.tradeflow.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public class FXTrade extends BaseTrade {

    private final String baseCurrency;
    private final String quoteCurrency;
    private final BigDecimal spotRate;

    private FXTrade(Builder b) {
        super(b.tradeRef, b.instrumentId, b.counterpartyId, b.quantity, b.price,
              b.tradeDate, b.status, b.createdAt);
        Objects.requireNonNull(b.baseCurrency, "baseCurrency required");
        Objects.requireNonNull(b.quoteCurrency, "quoteCurrency required");
        Objects.requireNonNull(b.spotRate, "spotRate required");

        if (b.baseCurrency.length() != 3 || b.quoteCurrency.length() != 3) {
            throw new IllegalStateException("currency codes must be 3-letter ISO-4217 codes");
        }
        
        if (b.baseCurrency.equals(b.quoteCurrency)) {
            throw new IllegalStateException("Quote and base currency must not be the same");
        }
        
        if (b.spotRate.signum() <= 0) {
            throw new IllegalStateException("Spot rate must be > 0");
        }
        this.baseCurrency  = b.baseCurrency;
        this.quoteCurrency = b.quoteCurrency;
        this.spotRate      = b.spotRate;
    }

    public static Builder builder() { return new Builder(); }

    public String getBaseCurrency()  { return baseCurrency; }
    public String getQuoteCurrency() { return quoteCurrency; }
    public BigDecimal getSpotRate()  { return spotRate; }

    @Override
    public String assetClassDescription() {
        return baseCurrency + "/" + quoteCurrency;
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
        private String baseCurrency;
        private String quoteCurrency;
        private BigDecimal spotRate;

        public Builder tradeRef(String v)        { this.tradeRef = v; return this; }
        public Builder instrumentId(Long v)      { this.instrumentId = v; return this; }
        public Builder counterpartyId(Long v)    { this.counterpartyId = v; return this; }
        public Builder quantity(BigDecimal v)    { this.quantity = v; return this; }
        public Builder price(BigDecimal v)       { this.price = v; return this; }
        public Builder tradeDate(LocalDate v)    { this.tradeDate = v; return this; }
        public Builder status(TradeStatus v)     { this.status = v; return this; }
        public Builder createdAt(Instant v)      { this.createdAt = v; return this; }
        public Builder baseCurrency(String v)    { this.baseCurrency = v; return this; }
        public Builder quoteCurrency(String v)   { this.quoteCurrency = v; return this; }
        public Builder spotRate(BigDecimal v)    { this.spotRate = v; return this; }

        public FXTrade build() { return new FXTrade(this); }
    }
}