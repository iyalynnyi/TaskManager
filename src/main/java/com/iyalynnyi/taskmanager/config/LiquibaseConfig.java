package com.iyalynnyi.taskmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
public class LiquibaseConfig {
  @Bean
  public SpringLiquibase liquibaseH2(DataSource h2DataSource) {
    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setDataSource(h2DataSource);
    liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.sql");
    return liquibase;
  }

  @Bean
  public SpringLiquibase liquibasePostgres(DataSource postgresDataSource) {
    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setDataSource(postgresDataSource);
    liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.sql");
    return liquibase;
  }
}
