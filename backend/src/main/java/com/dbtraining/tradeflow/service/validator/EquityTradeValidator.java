package com.dbtraining.tradeflow.service.validator;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.dbtraining.tradeflow.exception.TradeValidationException;
import com.dbtraining.tradeflow.exception.TradeValidationException.Code;
import com.dbtraining.tradeflow.model.BaseTrade;
import com.dbtraining.tradeflow.model.EquityTrade;

/**
 * ============================================================================
 * EquityTradeValidator — TICKET-I038
 * ============================================================================
 * WHAT:    Validation rules specific to EquityTrade.
 * WHY:     Equity rules differ from FX/Bond (lot size > 0, exchange known).
 * ============================================================================
 *  TODO(TICKET-I038):
 *    - if (!(trade instanceof EquityTrade et)) throw new TradeValidationException(...)
 *    - et.exchange() must be non-blank
 *    - et.lotSize() > 0
 *    - et.quantity() must be a whole multiple of lotSize
 * ============================================================================
 */
@Component
public class EquityTradeValidator implements ITradeValidator {

    @Override
    public void validate(BaseTrade trade) throws TradeValidationException {
        // TODO(TICKET-I038): implement.
        
        if (! (trade instanceof EquityTrade equityTrade)){
            throw new TradeValidationException(Code.INVALID_VALUE, "We only allow equity trades here");
        }

        if (equityTrade.getExchange() == null || equityTrade.getExchange().isBlank()){
            throw new TradeValidationException(Code.MISSING_FIELD, "exchange param required");
        }

        if (equityTrade.getLotSize() <= 0){
            throw new TradeValidationException(Code.INVALID_VALUE, "the lot size must be greater than 0");
        }

        BigDecimal lotSize = BigDecimal.valueOf(equityTrade.getLotSize());
        if (equityTrade.getQuantity().remainder(lotSize).signum() != 0){
            throw new TradeValidationException(Code.INVALID_VALUE, "quantity must be a whole multiple of the lot size");
        }


    }
}
