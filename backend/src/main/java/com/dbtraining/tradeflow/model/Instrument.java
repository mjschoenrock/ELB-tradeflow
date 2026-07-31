package com.dbtraining.tradeflow.model;

import java.util.Objects;
import jakarta.persistence.*;

/**
 * Instrument — POJO mirroring the instruments table.
 * Equality on symbol (e.g. "SAP.DE", "EURUSD"). ISIN may be null for FX
 * and commodities.
 */

@Entity
@Table(name = "instruments")
public class Instrument {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String symbol;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_class", nullable = false, length = 20)
    private AssetClass assetClass;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = true, unique = true, length = 12)
    private String isin;

    protected Instrument() {}

    private Instrument(Builder b) {
        this.symbol     = b.symbol;
        this.name       = b.name;
        this.assetClass = b.assetClass;
        this.currency   = b.currency;
        this.isin       = b.isin;
    }

    public static Builder builder() { return new Builder(); }

    public Long getId()              { return id; }
    public String getSymbol()        { return symbol; }
    public String getName()          { return name; }
    public AssetClass getAssetClass(){ return assetClass; }
    public String getCurrency()      { return currency; }
    public String getIsin()          { return isin; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Instrument other)) return false;
        return Objects.equals(symbol, other.symbol);
    }

    @Override public int hashCode() { return Objects.hash(symbol); }

    @Override public String toString() {
        return "Instrument[" + symbol + " | " + name + " | " + assetClass + " " + currency + "]";
    }

    public static final class Builder {
        private String symbol;
        private String name;
        private AssetClass assetClass;
        private String currency;
        private String isin;

        public Builder symbol(String v)         { this.symbol = v;     return this; }
        public Builder name(String v)           { this.name = v;       return this; }
        public Builder assetClass(AssetClass v) { this.assetClass = v; return this; }
        public Builder currency(String v)       { this.currency = v == null ? null : v.toUpperCase(); return this; }
        public Builder isin(String v)           { this.isin = v;       return this; }

        public Instrument build() {
            Objects.requireNonNull(symbol,     "symbol required");
            Objects.requireNonNull(name,       "name required");
            Objects.requireNonNull(assetClass, "assetClass required");
            Objects.requireNonNull(currency,   "currency required");
            if (currency.length() != 3)
                throw new IllegalStateException("currency must be ISO-4217 3-letter code");
            if (isin != null && isin.length() != 12)
                throw new IllegalStateException("ISIN must be exactly 12 chars when set");
            return new Instrument(this);
        }
    }
}
