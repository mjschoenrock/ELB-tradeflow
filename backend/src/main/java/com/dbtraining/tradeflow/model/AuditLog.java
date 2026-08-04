package com.dbtraining.tradeflow.model;

import org.hibernate.type.SqlTypes;
import java.time.Instant;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name="audit_log")
public class AuditLog {
    // TODO(TICKET-I059): fields + JPA annotations.

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="ENTITY")
    private String entity;

    @Column(name="ENTITY_ID")
    private Long entityId;

    @Column(name="ACTION")
    private String action;
    
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String oldValue;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String newValue;

    @Column(name="EVENT_TIME")
    private Instant eventTime;

    @Column(name = "CHANGED_AT")
    private Instant changedAt;

    @Column(name="USER_NAME")
    private String userName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEntity() { return entity; }
    public void setEntity(String entity) { this.entity = entity; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public Instant getEventTime() { return eventTime; }
    public void setEventTime(Instant eventTime) { this.eventTime = eventTime; }

    public Instant getChangedAt() { return changedAt; }
    public void setChangedAt(Instant changedAt) { this.changedAt = changedAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    // Manual builder, mirrors the Lombok-generated one
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final AuditLog instance = new AuditLog();

        public Builder entity(String entity) { instance.entity = entity; return this; }
        public Builder entityId(Long entityId) { instance.entityId = entityId; return this; }
        public Builder action(String action) { instance.action = action; return this; }
        public Builder oldValue(String oldValue) { instance.oldValue = oldValue; return this; }
        public Builder newValue(String newValue) { instance.newValue = newValue; return this; }
        public Builder timestamp(Instant timestamp) { instance.eventTime = timestamp; return this; }
        public Builder userName(String userName) { instance.userName = userName; return this; }
        public Builder changedAt(Instant changedAt) { instance.changedAt = changedAt; return this; }        

        public AuditLog build() { return instance; }
    }
   
}
