package com.capybara.springboot.storage.db.core.config;

import org.hibernate.Session;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.support.CustomSQLErrorCodesTranslation;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "com.capybara.springboot.storage.db.core")
@EnableJpaRepositories(basePackages = "com.capybara.springboot.storage.db.core")
class CoreJpaConfig {
}
