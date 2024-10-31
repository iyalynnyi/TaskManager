package com.iyalynnyi.taskmanager.service.impl;

import com.iyalynnyi.taskmanager.dao.model.TaskEntity;
import com.iyalynnyi.taskmanager.exception.ApiResponseException;
import com.iyalynnyi.taskmanager.service.RepositoryManagementService;
import com.iyalynnyi.taskmanager.service.TaskTransactionManagementService;
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

  /**
   * Saves a task entity to the primary database (H2) and falls back to the secondary database (PostgreSQL) if an error occurs.
   *
   * @param task the task entity to be saved
   * @return the saved task entity
   */
  public TaskEntity saveWithFallback(TaskEntity task) {
    try {
      TaskEntity taskEntity = transactionManagementService.executeInH2(repository -> repository.save(task));
      log.trace("Task saved in H2.");
      return taskEntity;
    } catch (Exception e) {
      log.trace("Error saving to primary database (H2), falling back to secondary database (Postgres)", e);
      TaskEntity taskEntity = transactionManagementService.executeInPostgres(repository -> repository.save(task));
      log.trace("Task saved in Postgres.");
      return taskEntity;
    }
  }

  /**
   * Finds a task entity by its ID, falling back to the secondary database if not found in the primary database.
   *
   * @param taskId the ID of the task to find
   * @return the found task entity
   * @throws ApiResponseException if the task is not found in either database
   */
  public TaskEntity findByIdWithFallback(Long taskId) {
    try {
      Optional<TaskEntity> taskEntity = transactionManagementService.executeInH2(repository -> repository.findById(taskId));
      if (taskEntity.isPresent()) {
        log.trace("Found task with id {} in H2.", taskId);
        return taskEntity.get();
      }
      log.error("Error finding task with id {} in H2. Trying to find in PostgreSQL.", taskId);
      throw new RuntimeException("Task not found in primary database, searching in failover database.");
    } catch (Exception e) {
      TaskEntity taskEntity = transactionManagementService.executeInPostgres(repository -> repository.findById(taskId))
          .orElseThrow(() -> new ApiResponseException("Task not found!", HttpStatus.BAD_REQUEST));
      log.trace("Found task with id {} in PostgreSQL.", taskId);
      return taskEntity;
    }
  }

  /**
   * Retrieves all task entities, falling back to the secondary database if the primary database fails.
   *
   * @return a list of all task entities
   */
  public List<TaskEntity> findAllWithFallback() {
    List<TaskEntity> tasks = new ArrayList<>();
    try {
      tasks.addAll(transactionManagementService.executeInH2(repository -> repository.findAll()));
      log.trace("Retrieved tasks from H2.");
    } catch (Exception e) {
      log.error("Error finding tasks in H2. Trying to find in PostgreSQL.", e);
    } finally {
      tasks.addAll(transactionManagementService.executeInPostgres(repository -> repository.findAll()));
      log.trace("Retrieved tasks from PostgreSQL.");
    }
    return tasks;
  }

  /**
   * Checks if a task exists by its ID, falling back to the secondary database if necessary.
   *
   * @param id the ID of the task to check
   * @return true if the task exists, false otherwise
   */
  public boolean existsById(Long id) {
    boolean exists;
    try {
      exists = transactionManagementService.executeInH2(repository -> repository.existsById(id));
      log.trace("Checked existence of task with id {} in H2: {}", id, exists);
      if (exists) {
        return true;
      }
    } catch (Exception e) {
      log.error("Error checking if Task with id {} exists in H2.", id, e);
    }
    exists = transactionManagementService.executeInPostgres(repository -> repository.existsById(id));
    log.trace("Checked existence of task with id {} in PostgreSQL: {}", id, exists);
    return exists;
  }

  /**
   * Deletes a task entity by its ID, falling back to the secondary database if the deletion fails in the primary database.
   *
   * @param id the ID of the task to be deleted
   */
  public void deleteById(Long id) {
    try {
      transactionManagementService.executeInH2WithoutResult(repository -> repository.deleteById(id));
      log.trace("Deleted task with id {} in H2.", id);
    } catch (Exception e) {
      log.error("Error deleting task with id {} in H2. Trying to delete in PostgreSQL.", id, e);
      transactionManagementService.executeInPostgresWithoutResult(repository -> repository.deleteById(id));
      log.trace("Deleted task with id {} in PostgreSQL.", id);
    }
  }
}
