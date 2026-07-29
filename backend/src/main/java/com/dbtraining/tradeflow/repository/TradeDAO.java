package com.dbtraining.tradeflow.repository;

import com.dbtraining.tradeflow.config.DatabaseConfig;
import com.dbtraining.tradeflow.model.Trade;
import com.dbtraining.tradeflow.model.TradeStatus;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * ============================================================================
 * TradeDAO — TICKET-I045 (Day 4 — raw JDBC)
 * ============================================================================
 * WHAT:    Raw JDBC data-access object for trades.
 * HOW:     PreparedStatement everywhere — NEVER string concatenation.
 * WHY:     Day 4 builds JDBC by hand so you understand what JPA hides on Day 5.
 * OBSERVE: Day 5's TradeRepository (Spring Data) replaces this class entirely.
 *          On Day 5 you can either delete this file or keep it for comparison.
 * ============================================================================
 *
 * HINTS:
 *  - try-with-resources for Connection, PreparedStatement, ResultSet.
 *  - For insert + generated key: Statement.RETURN_GENERATED_KEYS.
 *  - Map ResultSet → Trade via Trade.builder()...build().
 *  - Wrap SQLException in a RuntimeException with context (which method failed).
 * ============================================================================
 */






// this file is to be replaced anyway as per day 5 notes, so commented out as its old cold with some conflicts 





/*
public class TradeDAO {

    private static final String SELECT_COLUMNS =
            "id, trade_ref, instrument_id, counterparty_id, quantity, price, trade_date, status, created_at";

    private final DataSource dataSource;

    public TradeDAO() {
        this(DatabaseConfig.dataSource());
    }

    public TradeDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource must not be null");
    }

    public long insert(Trade trade) {
        String sql = "INSERT INTO trades " +
                "(trade_ref, instrument_id, counterparty_id, quantity, price, trade_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, trade.getTradeRef());
            statement.setLong(2, trade.getInstrumentId());
            statement.setLong(3, trade.getCounterpartyId());
            statement.setBigDecimal(4, trade.getQuantity());
            statement.setBigDecimal(5, trade.getPrice());
            statement.setDate(6, java.sql.Date.valueOf(trade.getTradeDate()));
            statement.setString(7, trade.getStatus().name());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new IllegalStateException("insert returned no generated key");
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("TradeDAO.insert failed for tradeRef=" + trade.getTradeRef(), exception);
        }
    }

    public Optional<Trade> findByRef(String tradeRef) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM trades WHERE trade_ref = ? LIMIT 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tradeRef);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("TradeDAO.findByRef failed for tradeRef=" + tradeRef, exception);
        }
    }

    public List<Trade> findAll() {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM trades ORDER BY trade_date DESC, id DESC";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Trade> trades = new ArrayList<>();
            while (resultSet.next()) {
                trades.add(mapRow(resultSet));
            }
            return trades;
        } catch (SQLException exception) {
            throw new IllegalStateException("TradeDAO.findAll failed", exception);
        }
    }

    public int updateStatus(String tradeRef, TradeStatus newStatus) {
        String sql = "UPDATE trades SET status = ? WHERE trade_ref = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newStatus.name());
            statement.setString(2, tradeRef);
            return statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("TradeDAO.updateStatus failed for tradeRef=" + tradeRef, exception);
        }
    }

    private static Trade mapRow(ResultSet resultSet) throws SQLException {
        return Trade.builder()
                .tradeRef(resultSet.getString("trade_ref"))
                .instrumentId(resultSet.getLong("instrument_id"))
                .counterpartyId(resultSet.getLong("counterparty_id"))
                .quantity(resultSet.getBigDecimal("quantity"))
                .price(resultSet.getBigDecimal("price"))
                .tradeDate(resultSet.getDate("trade_date").toLocalDate())
                .status(TradeStatus.valueOf(resultSet.getString("status")))
                .createdAt(resultSet.getTimestamp("created_at") == null
                        ? null
                        : resultSet.getTimestamp("created_at").toInstant())
                .build();
    }
}


*/