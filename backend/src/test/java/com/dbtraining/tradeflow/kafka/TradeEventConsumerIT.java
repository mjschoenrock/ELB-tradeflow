package com.dbtraining.tradeflow.kafka;

import com.dbtraining.tradeflow.config.KafkaConfig;
import com.dbtraining.tradeflow.dto.TradeDto;
import com.dbtraining.tradeflow.dto.TradeEvent;
import com.dbtraining.tradeflow.model.TradeStatus;
import com.dbtraining.tradeflow.service.AuditService;
import com.dbtraining.tradeflow.service.ReconciliationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * ============================================================================
 * TradeEventConsumerIT — TICKET-I122 (Day 9)
 * ============================================================================
 * WHAT:    Round-trip integration test — publishes a TradeEvent through the
 *          real producer, boots an in-JVM Kafka broker, and asserts both
 *          the recon and audit consumer groups received it (via @MockBean).
 * HOW:     @SpringBootTest wires only the Kafka slice (producer, consumers,
 *          KafkaConfig). @EmbeddedKafka spins up a broker; @TestPropertySource
 *          points spring.kafka.bootstrap-servers at ${spring.embedded.kafka.brokers}
 *          and forces auto-offset-reset=earliest so the poll picks up the
 *          record published just before the consumer joined the group.
 * WHY:     Catches the "works locally, breaks in CI" mode where groupIds
 *          collide across tests or auto-offset-reset defaults leave the test
 *          hanging until the 30-second poll timeout.
 * OBSERVE: `./mvnw test -Dtest=TradeEventConsumerIT` finishes in <30s with
 *          both consumer verifications green.
 * ============================================================================
 */
@SpringBootTest(classes = {
        KafkaConfig.class,
        TradeEventProducer.class,
        TradeEventConsumer.class,
        ReconEventConsumer.class,
        AuditEventConsumer.class
})
@EmbeddedKafka(partitions = 1, topics = { "trade-events", "trade-events.DLT" })
@Import(TradeEventConsumerIT.TestKafkaConfig.class)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "tradeflow.kafka.topics.trades=trade-events",
        "tradeflow.kafka.topics.dlt=trade-events.DLT",
        "spring.kafka.consumer.auto-offset-reset=earliest"
})
@DirtiesContext
class TradeEventConsumerIT {

    @Autowired private TradeEventProducer producer;
    @MockBean   private ReconciliationService reconciliationService;
    @MockBean   private AuditService auditService;

    @Test
    void publishedEvent_isReceivedByBothConsumerGroups() {
        TradeEvent event = new TradeEvent(
                "TRD-IT-0001",
                TradeEvent.Action.CREATED,
                Instant.now(),
                samplePayload("TRD-IT-0001"));

        producer.publish(event);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(reconciliationService).runForTrade(eq("TRD-IT-0001"));
            verify(auditService).record(any(TradeEvent.class));
        });
    }

    @Test
    void updatedEvent_skipsReconButStillAudits() {
        TradeEvent event = new TradeEvent(
                "TRD-IT-0002",
                TradeEvent.Action.UPDATED,
                Instant.now(),
                samplePayload("TRD-IT-0002"));

        producer.publish(event);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(auditService).record(any(TradeEvent.class));
            verifyNoInteractions(reconciliationService);
        });
    }

    private static TradeDto samplePayload(String tradeRef) {
        return new TradeDto(100L, tradeRef, 1L, 1L,
                new BigDecimal("100"), new BigDecimal("245.50"),
                LocalDate.of(2026, 3, 1), TradeStatus.PENDING, Instant.now());
    }

    /**
     * Minimal producer wiring for the sliced test — the KafkaConfig under
     * test only defines the consumer factory + error handler, so we supply
     * a KafkaTemplate<String, TradeEvent> here plus the KafkaTemplate<String,
     * Object> that the DefaultErrorHandler injects for DLT publishing.
     */
    @TestConfiguration
    static class TestKafkaConfig {
        @Bean
        KafkaTemplate<String, TradeEvent> kafkaTemplate(ProducerFactory<String, TradeEvent> pf) {
            return new KafkaTemplate<>(pf);
        }

        @Bean
        ProducerFactory<String, TradeEvent> producerFactory(KafkaProperties props) {
            return new DefaultKafkaProducerFactory<>(props.buildProducerProperties());
        }

        @Bean
        KafkaTemplate<String, Object> dltKafkaTemplate(KafkaProperties props) {
            return new KafkaTemplate<>(
                    new DefaultKafkaProducerFactory<>(props.buildProducerProperties()));
        }
    }
}
