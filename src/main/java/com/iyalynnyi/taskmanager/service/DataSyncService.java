package com.iyalynnyi.taskmanager.service;

import com.iyalynnyi.taskmanager.dao.model.TaskEntity;
import com.iyalynnyi.taskmanager.dao.repository.h2.H2TaskRepository;
import com.iyalynnyi.taskmanager.dao.repository.postgres.PostgresTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSyncService {

  private final PostgresTaskRepository postgresTaskRepository;
  private final H2TaskRepository h2TaskRepository;

  /**
   * Fetches all task entities from the PostgreSQL database.
   *
   * @return a list of task entities fetched from PostgreSQL
   */
  @Transactional(transactionManager = "postgresTransactionManager")
  public List<TaskEntity> fetchDataFromPostgres() {
    log.trace("Fetching data from PostgreSQL...");
    List<TaskEntity> tasks = postgresTaskRepository.findAll();
    log.trace("Fetched {} tasks from PostgreSQL.", tasks.size());
    return tasks;
  }

  /**
   * Saves a list of task entities to the H2 database.
   * The IDs of the tasks will be set to null to ensure new entries are created.
   *
   * @param tasks the list of task entities to save in H2
   */
  @Transactional(transactionManager = "h2TransactionManager")
  public void saveDataToH2(List<TaskEntity> tasks) {
    log.trace("Saving {} tasks to H2...", tasks.size());
    tasks.forEach(task -> task.setId(null));
    h2TaskRepository.saveAll(tasks);
    log.trace("Saved {} tasks to H2.", tasks.size());
  }

  /**
   * Deletes all task entities from the PostgreSQL database.
   */
  @Transactional(transactionManager = "postgresTransactionManager")
  public void deleteDataFromPostgres() {
    log.trace("Deleting all data from PostgreSQL...");
    postgresTaskRepository.deleteAll();
    log.trace("All tasks deleted from PostgreSQL.");
  }

  /**
   * Checks if the H2 repository is available by attempting to count the number of tasks.
   *
   * @return true if the H2 repository is available, false otherwise
   */
  public boolean isH2RepositoryAvailable() {
    try {
      h2TaskRepository.count();
      log.trace("H2 repository is available.");
      return true;
    } catch (Exception e) {
      log.warn("H2 repository is not available.", e);
      return false;
    }
  }
}

