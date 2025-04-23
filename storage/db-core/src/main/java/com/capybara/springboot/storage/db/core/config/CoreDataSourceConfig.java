package com.capybara.springboot.storage.db.core.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CoreDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "storage.datasource.core")
    public HikariConfig coreHikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = "dataSource") // Explicitly name the bean 'dataSource'
    public HikariDataSource dataSource(@Qualifier("coreHikariConfig") HikariConfig config) { // Rename method as well
        return new HikariDataSource(config);
    }

}
