package com.dbtraining.tradeflow.repository;

import com.dbtraining.tradeflow.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}