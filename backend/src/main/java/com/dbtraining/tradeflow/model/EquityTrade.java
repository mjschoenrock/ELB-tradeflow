package com.dbtraining.tradeflow.model;
import com.dbtraining.tradeflow.model.ReconResult.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import org.springframework.cglib.core.Local;

import com.dbtraining.tradeflow.model.ReconResult.Builder;
/**
 * ============================================================================
 * EquityTrade — TICKET-I029
 * ============================================================================
 * WHAT:    A trade on an equity (shares of a listed company).
 * WHY:     Equities are exchange-listed, so we need the exchange name + lot size.
 * ============================================================================
 *  TODO(TICKET-I029):
 *    extends BaseTrade.
 *    Extra fields:
 *      private final String exchange;     // e.g. "XETRA", "NASDAQ"
 *      private final int lotSize;         // typical 100 for US, 1 for European
 *
 *    assetClassDescription() returns "Equity on " + exchange.
 * ============================================================================
 */
public class EquityTrade extends BaseTrade {
    // TODO(TICKET-I029): extend BaseTrade, add exchange + lotSize, override
    //                    assetClassDescription().

    private final String exchange;
    private final int lotSize;

    protected EquityTrade(Builder builder){
        super(builder.tradeRef, builder.instrumentId, builder.counterpartyId, builder.quantity, builder.price, builder.tradeDate, builder.status, builder.createdAt);
        this.exchange = Objects.requireNonNull(builder.exchange, "exchange required");
        if (builder.lotSize <= 0) throw new IllegalStateException("lotSize must be > 0");
        this.lotSize = builder.lotSize;
    }

    @Override
    public String assetClassDescription() {
        return "Equity On " + this.exchange; 
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
        private String exchange;
        private int lotSize;

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
        public Builder exchange(String exchange){ this.exchange = exchange;       
            return this; 
        }
        public Builder lotSize(int lotSize){ this.lotSize = lotSize;        
            return this; 
        }

    }



}
