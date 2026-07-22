package com.dbtraining.tradeflow.model;

import java.util.Objects;

/**
 * Counterparty — POJO mirroring the counterparties table.
 * Equality on leiCode (globally-unique Legal Entity Identifier).
 */
public class Counterparty {

    private Long id;
    private String name;
    private String leiCode;
    private String region;

    Counterparty() {}

    private Counterparty(Builder b) {
        this.name    = b.name;
        this.leiCode = b.leiCode;
        this.region  = b.region;
    }

    public static Builder builder() { return new Builder(); }

    public Long getId()        { return id; }
    public String getName()    { return name; }
    public String getLeiCode() { return leiCode; }
    public String getRegion()  { return region; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Counterparty other)) return false;
        return Objects.equals(leiCode, other.leiCode);
    }

    @Override public int hashCode() { return Objects.hash(leiCode); }

    @Override public String toString() {
        return "Counterparty[" + leiCode + " | " + name + " | " + region + "]";
    }

    public static final class Builder {
        private String name;
        private String leiCode;
        private String region;

        public Builder name(String v)    { this.name = v;    return this; }
        public Builder leiCode(String v) { this.leiCode = v; return this; }
        public Builder region(String v)  { this.region = v;  return this; }

        public Counterparty build() {
            Objects.requireNonNull(name,    "name required");
            Objects.requireNonNull(leiCode, "leiCode required");
            Objects.requireNonNull(region,  "region required");
            if (leiCode.length() != 20)
                throw new IllegalStateException("leiCode must be exactly 20 chars (LEI standard)");
            if (!region.matches("APAC|EMEA|NAMR|LATAM"))
                throw new IllegalStateException("region must be one of APAC|EMEA|NAMR|LATAM");
            return new Counterparty(this);
        }
    }
}