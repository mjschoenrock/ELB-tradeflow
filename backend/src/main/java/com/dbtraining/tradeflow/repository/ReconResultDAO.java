package com.dbtraining.tradeflow.repository;

import com.dbtraining.tradeflow.config.DatabaseConfig;
import com.dbtraining.tradeflow.model.ReconResult;
import com.dbtraining.tradeflow.model.DiscrepancyType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ============================================================================
 * ReconResultDAO — TICKET-I046 (Day 4)
 * ============================================================================
 * WHAT:    JDBC DAO for recon_results.
 * HOW:     PreparedStatement + try-with-resources.
 * WHY:     The matching engine on Day 3 writes results here.
 * ============================================================================
 */
public class ReconResultDAO {

    private static final String SELECT_COLUMNS =
            "id, trade_id, status, discrepancy_type, resolved_at, created_at";

    private final DataSource dataSource;

    public ReconResultDAO() {
        this(DatabaseConfig.dataSource());
    }

    public ReconResultDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource must not be null");
    }

    public long insert(ReconResult result) {
        String sql = "INSERT INTO recon_breaks (trade_id, discrepancy_type, status) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, result.getTradeId());
            if (result.getDiscrepancyType() == null) {
                statement.setObject(2, null);
            } else {
                statement.setString(2, result.getDiscrepancyType().name());
            }
            statement.setString(3, result.getStatus());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new IllegalStateException("ReconResultDAO.insert returned no generated key");
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("ReconResultDAO.insert failed for tradeId=" + result.getTradeId(), exception);
        }
    }

    public List<ReconResult> findByTradeId(long tradeId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM recon_breaks WHERE trade_id = ? ORDER BY created_at DESC, id DESC";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, tradeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<ReconResult> reconResults = new ArrayList<>();
                while (resultSet.next()) {
                    reconResults.add(mapRow(resultSet));
                }
                return reconResults;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("ReconResultDAO.findByTradeId failed for tradeId=" + tradeId, exception);
        }
    }

    public List<ReconResult> findUnresolved() {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM recon_breaks WHERE status = 'OPEN' ORDER BY created_at DESC, id DESC";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<ReconResult> reconResults = new ArrayList<>();
            while (resultSet.next()) {
                reconResults.add(mapRow(resultSet));
            }
            return reconResults;
        } catch (SQLException exception) {
            throw new IllegalStateException("ReconResultDAO.findUnresolved failed", exception);
        }
    }

    private static ReconResult mapRow(ResultSet resultSet) throws SQLException {
        String discrepancyType = resultSet.getString("discrepancy_type");
        return ReconResult.builder()
                .id(resultSet.getLong("id"))
                .tradeId(resultSet.getLong("trade_id"))
                .status(resultSet.getString("status"))
                .discrepancyType(discrepancyType == null ? null : DiscrepancyType.valueOf(discrepancyType))
                .resolvedAt(resultSet.getTimestamp("resolved_at") == null
                        ? null
                        : resultSet.getTimestamp("resolved_at").toInstant())
                .createdAt(resultSet.getTimestamp("created_at") == null
                        ? null
                        : resultSet.getTimestamp("created_at").toInstant())
                .build();
    }
}
