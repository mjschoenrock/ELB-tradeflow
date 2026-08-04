package com.dbtraining.tradeflow.service;

import com.dbtraining.tradeflow.dto.TradeEvent;
import com.dbtraining.tradeflow.model.AuditLog;
import com.dbtraining.tradeflow.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public AuditService(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void record(TradeEvent event) {
        try {
            String newValueJson = objectMapper.writeValueAsString(event.payload());

            AuditLog entry = AuditLog.builder()
                    .entity("TRADE")
                    .entityId(event.payload() != null ? event.payload().id() : null)
                    .action(mapAction(event.action()))
                    .newValue(newValueJson)
                    .timestamp(event.timestamp() != null ? event.timestamp() : Instant.now())
                    .changedAt(Instant.now())
                    .userName("system") // TODO: replace once auth/user context is available
                    .build();

            auditLogRepository.save(entry);
            log.info("Audit record tradeRef={} action={}", event.tradeRef(), event.action());
        } catch (Exception e) {
            log.error("Failed to write audit record for tradeRef={}", event.tradeRef(), e);
        }
    }

    private String mapAction(TradeEvent.Action action) {
        return switch (action) {
            case CREATED -> "INSERT";
            case UPDATED -> "UPDATE";
            case CANCELLED -> "DELETE";
        };
    }
}