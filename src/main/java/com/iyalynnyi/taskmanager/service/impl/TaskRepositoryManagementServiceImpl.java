package com.iyalynnyi.taskmanager.service.impl;

import com.iyalynnyi.taskmanager.dao.model.TaskEntity;
import com.iyalynnyi.taskmanager.exception.ApiResponseException;
import com.iyalynnyi.taskmanager.service.RepositoryManagementService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskRepositoryManagementServiceImpl implements RepositoryManagementService<TaskEntity> {

  private final TaskTransactionManagementService transactionManagementService;

  public TaskEntity saveWithFallback(TaskEntity task) {
    try {
      return transactionManagementService.executeInH2(repository -> repository.save(task));
    } catch (Exception e) {
      log.error("Error saving to primary database (H2), falling back to secondary database (Postgres)", e);
      return transactionManagementService.executeInPostgres(repository -> repository.save(task));
    }
  }

  public TaskEntity findByIdWithFallback(Long task) {
    try {
      Optional<TaskEntity> taskEntity = transactionManagementService.executeInH2(repository -> repository.findById(task));
      if (taskEntity.isPresent()) {
        return taskEntity.get();
      }
      log.error("Error finding task with id {} in H2. Trying to find in PostgreSql", task);
      throw new RuntimeException("Task not found in primary database, searching in failover database.");
    } catch (Exception e) {
      return transactionManagementService.executeInPostgres(repository -> repository.findById(task))
          .orElseThrow(() -> new ApiResponseException("Task not found!", HttpStatus.BAD_REQUEST));
    }
  }

  public List<TaskEntity> findAllWithFallback() {
    List<TaskEntity> tasks = new ArrayList<>();
    try {
      tasks.addAll(transactionManagementService.executeInH2(repository -> repository.findAll()));
    } catch (Exception e) {
      log.error("Error finding tasks in H2. Trying to find in Postgres.", e);
    } finally {
      tasks.addAll(transactionManagementService.executeInPostgres(repository -> repository.findAll()));
    }
    return tasks;
  }

  public boolean existsById(Long id) {
    boolean exists;
    try {
      exists = transactionManagementService.executeInH2(repository -> repository.existsById(id));
      if (exists) {
        return true;
      }
    } catch (Exception e) {
      log.error("Error checking if Task with id {} exists in H2.", id, e);
    }
    return transactionManagementService.executeInPostgres(repository -> repository.existsById(id));
  }

  public void deleteById(Long id) {
    try {
      transactionManagementService.executeInH2WithoutResult(repository -> repository.deleteById(id));
    } catch (Exception e) {
      log.error("Error deleting task with id {} in H2.", id, e);
      transactionManagementService.executeInPostgresWithoutResult(repository -> repository.deleteById(id));
    }
  }

}
