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

  /**
   * Synchronizes data from the PostgreSQL database to the H2 database.
   * This method is scheduled to run at a fixed rate of one hour.
   */
  @Scheduled(fixedRate = 3600000)
  public void synchronize() {
    log.info("Starting synchronization (PostgreSQL -> H2)...");

    if (dataSyncService.isH2RepositoryAvailable()) {
      log.info("H2 repository is available. Proceeding with data synchronization.");

      List<TaskEntity> tasks = dataSyncService.fetchDataFromPostgres();
      dataSyncService.saveDataToH2(tasks);
      dataSyncService.deleteDataFromPostgres();
      log.info("Finished synchronization (PostgreSQL -> H2).");
    } else {
      log.warn("H2 repository not available. Data sync cancelled.");
    }
  }
}

