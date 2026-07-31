package com.dbtraining.tradeflow.service.validator;

import com.dbtraining.tradeflow.exception.TradeValidationException;
import com.dbtraining.tradeflow.exception.TradeValidationException.Code;
import com.dbtraining.tradeflow.model.BaseTrade;
import com.dbtraining.tradeflow.model.BondTrade;
import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * BondTradeValidator — TICKET-I038 (mirror of EquityTradeValidator)
 * ============================================================================
 * WHAT:    Validation rules specific to BondTrade.
 * WHY:     Bond rules differ from Equity/FX (coupon rate range, maturity
 *          after trade date, face value).
 * ============================================================================
 *  NOTE: BondTrade (TICKET-I031) does not yet expose getCouponRate(),
 *  getMaturityDate(), or getFaceValue() — those field-level checks can't be
 *  written here until that ticket adds the getters. For now this only
 *  enforces the type guard, same as the first check in EquityTradeValidator/
 *  FXTradeValidator.
 * ============================================================================
 */
@Component
public class BondTradeValidator implements ITradeValidator {

    @Override
    public void validate(BaseTrade trade) throws TradeValidationException {
        if (!(trade instanceof BondTrade)) {
            throw new TradeValidationException(Code.INVALID_VALUE,
                    "BondTradeValidator only accepts BondTrade");
        }
        // TODO(TICKET-I031): once BondTrade exposes getCouponRate() /
        //   getMaturityDate() / getFaceValue(), add:
        //     - couponRate between 0 and 100 -> else INVALID_VALUE
        //     - maturityDate must be after tradeDate -> else INVALID_VALUE
        //     - faceValue > 0 -> else INVALID_VALUE
    }
}
