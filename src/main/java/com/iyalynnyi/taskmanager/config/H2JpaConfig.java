package com.iyalynnyi.taskmanager.config;

import com.iyalynnyi.taskmanager.config.properties.H2DataSourceProperties;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;

import javax.sql.DataSource;

import jakarta.persistence.EntityManagerFactory;

@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.iyalynnyi.taskmanager.dao.repository.h2",
    entityManagerFactoryRef = "h2EntityManagerFactory",
    transactionManagerRef = "h2TransactionManager"
)
public class H2JpaConfig {

  @Bean(name = "h2DataSource")
  public DataSource h2DataSource(H2DataSourceProperties h2DataSourceProperties) {

    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(h2DataSourceProperties.getDriverClassName());
    dataSource.setUrl(h2DataSourceProperties.getUrl());
    dataSource.setUsername(h2DataSourceProperties.getUsername());
    dataSource.setPassword(h2DataSourceProperties.getPassword());

    return dataSource;
  }

  @Bean(name = "h2EntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean h2EntityManagerFactory(EntityManagerFactoryBuilder builder,
      @Qualifier("h2DataSource") DataSource h2DataSource) {
    return builder
        .dataSource(h2DataSource)
        .packages("com.iyalynnyi.taskmanager.dao.model")
        .persistenceUnit("h2Unit")
        .build();
  }

  @Bean
  public JpaTransactionManager h2TransactionManager(
      @Qualifier("h2EntityManagerFactory") EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

  @Bean
  public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
    return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
  }
}

