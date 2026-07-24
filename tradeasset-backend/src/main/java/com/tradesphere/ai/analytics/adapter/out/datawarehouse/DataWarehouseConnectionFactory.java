package com.tradesphere.ai.analytics.adapter.out.datawarehouse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating data warehouse connections
 * Supports multiple warehouse backends
 */
@Component
@Slf4j
public class DataWarehouseConnectionFactory {

    @Value("${analytics.warehouse.type:postgresql}")
    private String warehouseType;

    @Value("${analytics.warehouse.url:jdbc:postgresql://localhost:5432/analytics}")
    private String warehouseUrl;

    @Value("${analytics.warehouse.username:analytics_user}")
    private String username;

    @Value("${analytics.warehouse.password:analytics_pass}")
    private String password;

    private final Map<String, DataSource> dataSourceCache = new HashMap<>();

    /**
     * Get JDBC template for warehouse operations
     */
    public JdbcTemplate getJdbcTemplate() {
        DataSource dataSource = dataSourceCache.computeIfAbsent(warehouseType, this::createDataSource);
        return new JdbcTemplate(dataSource);
    }

    /**
     * Create data source based on warehouse type
     */
    private DataSource createDataSource(String type) {
        log.info("Creating data warehouse connection: type={}", type);

        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        switch (type.toLowerCase()) {
            case "postgresql" -> {
                dataSource.setDriverClassName("org.postgresql.Driver");
                dataSource.setUrl(warehouseUrl);
            }
            case "snowflake" -> {
                dataSource.setDriverClassName("net.snowflake.client.jdbc.SnowflakeDriver");
                dataSource.setUrl(warehouseUrl);
            }
            case "bigquery" -> {
                dataSource.setDriverClassName("com.simba.googlebigquery.jdbc.Driver");
                dataSource.setUrl(warehouseUrl);
            }
            case "redshift" -> {
                dataSource.setDriverClassName("com.amazon.redshift.jdbc.Driver");
                dataSource.setUrl(warehouseUrl);
            }
            default -> throw new IllegalArgumentException("Unsupported warehouse type: " + type);
        }

        dataSource.setUsername(username);
        dataSource.setPassword(password);

        log.info("Data warehouse connection created successfully");
        return dataSource;
    }

    /**
     * Test warehouse connection
     */
    public boolean testConnection() {
        try {
            JdbcTemplate template = getJdbcTemplate();
            template.queryForObject("SELECT 1", Integer.class);
            log.info("Warehouse connection test successful");
            return true;
        } catch (Exception e) {
            log.error("Warehouse connection test failed", e);
            return false;
        }
    }

    /**
     * Initialize warehouse schema
     */
    public void initializeSchema() {
        log.info("Initializing warehouse schema");

        JdbcTemplate template = getJdbcTemplate();

        // Create analytics schema
        template.execute("CREATE SCHEMA IF NOT EXISTS analytics");

        // Create metrics table
        template.execute("""
            CREATE TABLE IF NOT EXISTS analytics.metrics (
                id BIGSERIAL PRIMARY KEY,
                tenant_id UUID NOT NULL,
                metric_name VARCHAR(255) NOT NULL,
                metric_value DOUBLE PRECISION NOT NULL,
                metric_type VARCHAR(50),
                recorded_at TIMESTAMP NOT NULL,
                dimensions JSONB,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                UNIQUE (tenant_id, metric_name, recorded_at)
            )
            """);

        // Create archive table
        template.execute("""
            CREATE TABLE IF NOT EXISTS analytics.metrics_archive (
                LIKE analytics.metrics INCLUDING ALL
            )
            """);

        // Create indices
        template.execute("""
            CREATE INDEX IF NOT EXISTS idx_metrics_tenant_metric 
            ON analytics.metrics(tenant_id, metric_name, recorded_at DESC)
            """);

        template.execute("""
            CREATE INDEX IF NOT EXISTS idx_metrics_recorded_at 
            ON analytics.metrics(recorded_at DESC)
            """);

        template.execute("""
            CREATE INDEX IF NOT EXISTS idx_metrics_dimensions 
            ON analytics.metrics USING gin(dimensions)
            """);

        log.info("Warehouse schema initialized successfully");
    }
}

