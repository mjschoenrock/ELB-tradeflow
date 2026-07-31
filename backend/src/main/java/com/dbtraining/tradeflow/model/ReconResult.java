package com.dbtraining.tradeflow.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "recon_breaks")
public class ReconResult {

    public enum Status { OPEN, RESOLVED, SUPPRESSED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trade_id")
    private Trade trade;

    @Enumerated(EnumType.STRING)
    @Column(name = "discrepancy_type", nullable = false, length = 30)
    private DiscrepancyType discrepancyType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    // NOTE: the physical column is "created_at" (see 006-create-recon-breaks.xml
    // and the reference v_settlement_lag view, which aliases
    // "rb.created_at AS detected_at") — detectedAt is the app-level name.
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant detectedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;       // nullable

    protected ReconResult() {}

    private ReconResult(Builder b) {
        this.trade           = b.trade;
        this.discrepancyType = b.discrepancyType;
        this.status          = b.status != null ? b.status : Status.OPEN;
        this.detectedAt      = b.detectedAt != null ? b.detectedAt : Instant.now();
        this.resolvedAt      = b.resolvedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Trade trade;
        private DiscrepancyType discrepancyType;
        private Status status;
        private Instant detectedAt;
        private Instant resolvedAt;

        public Builder trade(Trade v)                       { this.trade = v; return this; }
        public Builder discrepancyType(DiscrepancyType v)   { this.discrepancyType = v; return this; }
        public Builder status(Status v)                     { this.status = v; return this; }
        public Builder detectedAt(Instant v)                { this.detectedAt = v; return this; }
        public Builder resolvedAt(Instant v)                { this.resolvedAt = v; return this; }

        public ReconResult build() { return new ReconResult(this); }
    }

    public void resolve() {
        if (this.status == Status.RESOLVED) return;
        this.status = Status.RESOLVED;
        this.resolvedAt = Instant.now();
    }

    public boolean isOpen() { return status == Status.OPEN; }

    public Long getId()                          { return id; }
    public Trade getTrade()                      { return trade; }
    public DiscrepancyType getDiscrepancyType()  { return discrepancyType; }
    public Status getStatus()                    { return status; }
    public Instant getDetectedAt()               { return detectedAt; }
    public Instant getResolvedAt()               { return resolvedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReconResult other)) return false;
        return Objects.equals(trade, other.trade)
            && discrepancyType == other.discrepancyType
            && Objects.equals(detectedAt, other.detectedAt);
    }

    @Override
    public int hashCode() { return Objects.hash(trade, discrepancyType, detectedAt); }
}