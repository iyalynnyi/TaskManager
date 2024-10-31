package com.iyalynnyi.taskmanager.job;

import com.iyalynnyi.taskmanager.dao.model.TaskEntity;
import com.iyalynnyi.taskmanager.service.DataSyncService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSyncJob {
  private final DataSyncService dataSyncService;

  @Scheduled(fixedRate = 3600000)
  public void synchronize() {
    log.info("Starting synchronization (PostgreSql -> H2)...");
    if (dataSyncService.isH2RepositoryAvailable()) {
      List<TaskEntity> tasks = dataSyncService.fetchDataFromPostgres();
      dataSyncService.saveDataToH2(tasks);
      dataSyncService.deleteDataFromPostgres();
      log.info("Finished synchronization (PostgreSql -> H2).");
    } else {
      log.info("H2 repository not available. Data sync cancelled.");
    }
  }

}
