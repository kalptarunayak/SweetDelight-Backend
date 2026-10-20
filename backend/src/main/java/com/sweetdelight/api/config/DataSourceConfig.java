package com.sweetdelight.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/postgres}")
    private String rawUrl;

    @Value("${spring.datasource.username:postgres}")
    private String defaultUsername;

    @Value("${spring.datasource.password:postgres}")
    private String defaultPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        String jdbcUrl = rawUrl;
        String username = defaultUsername;
        String password = defaultPassword;

        try {
            // Handle postgres:// or postgresql:// formats injected by Supabase/Render
            if (rawUrl != null && (rawUrl.startsWith("postgres://") || rawUrl.startsWith("postgresql://"))) {
                String cleanUriString = rawUrl.startsWith("postgres://") ? "http://" + rawUrl.substring(11) : "http://" + rawUrl.substring(13);
                URI uri = new URI(cleanUriString);

                String host = uri.getHost();
                int port = uri.getPort() != -1 ? uri.getPort() : 5432;
                String path = uri.getPath(); // e.g. /postgres

                if (uri.getUserInfo() != null) {
                    String[] userInfo = uri.getUserInfo().split(":", 2);
                    username = userInfo[0];
                    if (userInfo.length > 1) {
                        password = userInfo[1];
                    }
                }

                jdbcUrl = String.format("jdbc:postgresql://%s:%d%s", host, port, path);
                log.info("Converted database connection URL to standard JDBC format: jdbc:postgresql://{}:{}{}", host, port, path);
            }
        } catch (Exception e) {
            log.warn("Could not parse raw database URL as URI, using configured value directly: {}", e.getMessage());
        }

        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .build();
    }
}
