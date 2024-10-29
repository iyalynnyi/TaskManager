package com.iyalynnyi.taskmanager.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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
    basePackages = "com.iyalynnyi.taskmanager.dao.repository",
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager"
)
public class JpaConfig {

  @Autowired
  private DataSource dataSource;

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
    return builder
        .dataSource(dataSource)
        .packages("com.iyalynnyi.taskmanager.dao.model")
        .persistenceUnit("taskManager")
        .build();
  }

  @Bean
  public JpaTransactionManager transactionManager(
      @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

  @Bean
  public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
    return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
  }
}

