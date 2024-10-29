package com.iyalynnyi.taskmanager.config.properties;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "main.datasource")
public class H2DataSourceProperties {
  private String url;
  private String driverClassName;
  private String username;
  private String password;
}
