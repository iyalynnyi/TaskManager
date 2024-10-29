package com.iyalynnyi.taskmanager.config;

import com.iyalynnyi.taskmanager.config.properties.H2DataSourceProperties;
import com.iyalynnyi.taskmanager.config.properties.PostgresDataSourceProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class DataSourceConfig {


  @Bean(name = "h2DataSource")
  public DataSource h2DataSource(H2DataSourceProperties h2DataSourceProperties) {

    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(h2DataSourceProperties.getDriverClassName());
    dataSource.setUrl(h2DataSourceProperties.getUrl());
    dataSource.setUsername(h2DataSourceProperties.getUsername());
    dataSource.setPassword(h2DataSourceProperties.getPassword());

    return dataSource;
  }

  @Bean(name = "postgresDataSource")
  public DataSource postgresDataSource(PostgresDataSourceProperties postgresDataSourceProperties) {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(postgresDataSourceProperties.getDriverClassName());
    dataSource.setUrl(postgresDataSourceProperties.getUrl());
    dataSource.setUsername(postgresDataSourceProperties.getUsername());
    dataSource.setPassword(postgresDataSourceProperties.getPassword());

    return dataSource;  }

  @Bean(name = "dataSource")
  public DataSource routingDataSource(
      @Autowired DataSource h2DataSource,
      @Autowired DataSource postgresDataSource) {

    Map<Object, Object> targetDataSources = new HashMap<>();
    targetDataSources.put("H2", h2DataSource);
    targetDataSources.put("Postgres", postgresDataSource);

    AbstractRoutingDataSource routingDataSource = new AbstractRoutingDataSource() {
      @Override
      protected Object determineCurrentLookupKey() {
        try {
          h2DataSource.getConnection().close();
          return "H2";
        } catch (Exception e) {
          log.warn("Could not connect to H2 database. Postgres will be used as a failover database.", e);
          return "Postgres";
        }
      }
    };

    routingDataSource.setTargetDataSources(targetDataSources);
    routingDataSource.setDefaultTargetDataSource(h2DataSource);

    routingDataSource.afterPropertiesSet();
    return new LazyConnectionDataSourceProxy(routingDataSource);
  }
}
