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

  @Transactional(transactionManager = "postgresTransactionManager")
  public List<TaskEntity> fetchDataFromPostgres() {
    log.info("Fetching data from postgres...");

    return postgresTaskRepository.findAll();
  }

  @Transactional(transactionManager = "h2TransactionManager")
  public void saveDataToH2(List<TaskEntity> tasks) {
    tasks.forEach(task -> task.setId(null));
    h2TaskRepository.saveAll(tasks);
  }

  @Transactional(transactionManager = "postgresTransactionManager")
  public void deleteDataFromPostgres() {
    log.info("Deleting data from postgres...");
    postgresTaskRepository.deleteAll();
  }

  public boolean isH2RepositoryAvailable() {
    try {
      h2TaskRepository.count();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

}
