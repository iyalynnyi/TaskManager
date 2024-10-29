package com.iyalynnyi.taskmanager.config.properties;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "failover.datasource")
public class PostgresDataSourceProperties {
  private String url;
  private String driverClassName;
  private String username;
  private String password;
}
