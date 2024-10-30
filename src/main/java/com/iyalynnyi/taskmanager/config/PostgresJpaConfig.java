package com.iyalynnyi.taskmanager.config;

import com.iyalynnyi.taskmanager.config.properties.PostgresDataSourceProperties;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

import jakarta.persistence.EntityManagerFactory;

@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.iyalynnyi.taskmanager.dao.repository.postgres",
    entityManagerFactoryRef = "postgresEntityManagerFactory",
    transactionManagerRef = "postgresTransactionManager"
)
public class PostgresJpaConfig {

  @Bean(name = "postgresDataSource")
  public DataSource postgresDataSource(PostgresDataSourceProperties postgresDataSourceProperties) {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(postgresDataSourceProperties.getDriverClassName());
    dataSource.setUrl(postgresDataSourceProperties.getUrl());
    dataSource.setUsername(postgresDataSourceProperties.getUsername());
    dataSource.setPassword(postgresDataSourceProperties.getPassword());

    return dataSource;  }

  @Bean(name = "postgresEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean postgresEntityManagerFactory(EntityManagerFactoryBuilder builder,
      @Qualifier("postgresDataSource") DataSource postgresDataSource) {
    return builder
        .dataSource(postgresDataSource)
        .packages("com.iyalynnyi.taskmanager.dao.model")
        .persistenceUnit("postgresUnit")
        .build();
  }

  @Bean
  public JpaTransactionManager postgresTransactionManager(
      @Qualifier("postgresEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

}

