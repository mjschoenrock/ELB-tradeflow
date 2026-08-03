package com.dbtraining.tradeflow.service;

import com.dbtraining.tradeflow.dto.TradeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Minimal audit service used by Kafka consumer wiring.
 * Day-9 can later replace this with DB persistence logic.
 */
@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    public void record(TradeEvent event) {
        log.info("Audit record tradeRef={} action={}", event.tradeRef(), event.action());
    }
}
