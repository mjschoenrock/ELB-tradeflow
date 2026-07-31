package com.dbtraining.tradeflow;

import com.dbtraining.tradeflow.dto.ReconSummary;
import com.dbtraining.tradeflow.service.TradeProcessor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;

/**
 * ============================================================================
 * TradeflowApplication — Spring Boot entry point
 * ============================================================================
 * WHAT:    The single annotated main() that bootstraps the whole service.
 * HOW:     `@SpringBootApplication` = `@Configuration` + `@EnableAutoConfiguration`
 *          + `@ComponentScan` — scans this package and below for beans.
 * WHY:     One starting point, predictable lifecycle, easy to launch from
 *          IDE or `./mvnw spring-boot:run`.
 * OBSERVE: Boot log includes "Started TradeflowApplication in X seconds".
 * ============================================================================
 *  Tickets that touch this file:
 *   - TICKET-I016 — package structure + boot main
 *   - TICKET-I026 — print formatted trade list (Day 2, BEFORE Spring boot wiring)
 *   - TICKET-I040 — wire up the full recon pipeline run in main (Day 3 sprint)
 * ============================================================================
 */
@SpringBootApplication
public class TradeflowApplication {

    public static void main(String[] args) {
        printBanner();
        SpringApplication.run(TradeflowApplication.class, args);
    }

    /**
     * TICKET-I040 — Manual run on sample data.
     * Runs the full parse -> validate -> reconcile -> report pipeline against
     * the two hand-crafted fixture CSVs and prints the resulting ReconSummary,
     * proving I028-I039 are wired together end-to-end.
     */
    @Bean
    CommandLineRunner reconDemoRunner(TradeProcessor processor) {
        return args -> {
            Path internal = Path.of("src/test/resources/internal-trades.csv");
            Path external = Path.of("src/test/resources/external-trades.csv");
            ReconSummary summary = processor.process(internal, external);
            System.out.println();
            System.out.println("== Day-3 recon demo (TICKET-I040) ==================================================");
            System.out.println(summary);
            System.out.println("====================================================================================");
        };
    }

    private static void printBanner() {
        System.out.println();
        System.out.println("  ████████ ██████   █████  ██████  ███████ ███████ ██       ██████  ██     ██");
        System.out.println("     ██    ██   ██ ██   ██ ██   ██ ██      ██      ██      ██    ██ ██     ██");
        System.out.println("     ██    ██████  ███████ ██   ██ █████   █████   ██      ██    ██ ██  █  ██");
        System.out.println("     ██    ██   ██ ██   ██ ██   ██ ██      ██      ██      ██    ██ ██ ███ ██");
        System.out.println("     ██    ██   ██ ██   ██ ██████  ███████ ██      ███████  ██████   ███ ███");
        System.out.println();
        System.out.println("  Deutsche Bank — TDI 2026 Graduate Technical Training");
        System.out.println("  Intermediate Track — Case Study: Trade Reconciliation");
        System.out.println();
    }

}
