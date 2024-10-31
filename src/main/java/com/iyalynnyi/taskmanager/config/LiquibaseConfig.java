package com.iyalynnyi.taskmanager.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;

@Slf4j
@Configuration
public class LiquibaseConfig {
  @Bean
  public SpringLiquibase liquibaseH2(DataSource h2DataSource) {
    log.info("Url for connection to h2 is: [{}]", ((DriverManagerDataSource)h2DataSource).getUrl());
    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setDataSource(h2DataSource);
    liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.sql");
    return liquibase;
  }

  @Bean
  public SpringLiquibase liquibasePostgres(DataSource postgresDataSource) {
    log.info("Url for connection to postgres is: [{}]", ((DriverManagerDataSource)postgresDataSource).getUrl());
    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setDataSource(postgresDataSource);
    liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.sql");
    return liquibase;
  }
}
