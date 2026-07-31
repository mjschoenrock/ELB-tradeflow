package com.dbtraining.tradeflow.repository;

import com.dbtraining.tradeflow.config.DatabaseConfig;
import com.dbtraining.tradeflow.model.Counterparty;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import java.util.List;

/**
 * ============================================================================
 * CounterpartyDAO — TICKET-I047 (Day 4)
 * ============================================================================
 */
public class CounterpartyDAO {

    private static final Set<String> VALID_REGIONS = Set.of("APAC", "EMEA", "NAMR", "LATAM");
    private static final String SELECT_COLUMNS = "name, lei_code, region";

    private final DataSource dataSource;

    public CounterpartyDAO() {
        this(DatabaseConfig.dataSource());
    }

    public CounterpartyDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource must not be null");
    }

    public List<Counterparty> findAll() {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM counterparties ORDER BY name";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Counterparty> counterparties = new ArrayList<>();
            while (resultSet.next()) {
                counterparties.add(mapRow(resultSet));
            }
            return counterparties;
        } catch (SQLException exception) {
            throw new IllegalStateException("CounterpartyDAO.findAll failed", exception);
        }
    }

    public List<Counterparty> findByRegion(String region) {
        if (region == null || !VALID_REGIONS.contains(region)) {
            throw new IllegalArgumentException("region must be one of " + VALID_REGIONS + " (was " + region + ")");
        }

        String sql = "SELECT " + SELECT_COLUMNS + " FROM counterparties WHERE region = ? ORDER BY name";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, region);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Counterparty> counterparties = new ArrayList<>();
                while (resultSet.next()) {
                    counterparties.add(mapRow(resultSet));
                }
                return counterparties;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("CounterpartyDAO.findByRegion failed for region=" + region, exception);
        }
    }

    private static Counterparty mapRow(ResultSet resultSet) throws SQLException {
        return Counterparty.builder()
                .name(resultSet.getString("name"))
                .leiCode(resultSet.getString("lei_code"))
                .region(resultSet.getString("region"))
                .build();
    }
}
