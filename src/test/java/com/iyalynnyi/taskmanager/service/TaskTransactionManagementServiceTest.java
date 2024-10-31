package com.iyalynnyi.taskmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.iyalynnyi.taskmanager.dao.model.TaskEntity;
import com.iyalynnyi.taskmanager.dao.repository.h2.H2TaskRepository;
import com.iyalynnyi.taskmanager.dao.repository.postgres.PostgresTaskRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TaskTransactionManagementServiceTest {
  private H2TaskRepository h2TaskRepository;
  private PostgresTaskRepository postgresTaskRepository;
  private TaskTransactionManagementService transactionManagementService;

  @BeforeEach
  void setUp() {
    h2TaskRepository = Mockito.mock(H2TaskRepository.class);
    postgresTaskRepository = Mockito.mock(PostgresTaskRepository.class);
    transactionManagementService = new TaskTransactionManagementService(h2TaskRepository, postgresTaskRepository);
  }

  @Test
  void executeInH2_shouldExecuteActionWithH2Repository() {
    TaskEntity expectedTask = new TaskEntity();
    expectedTask.setId(1L);
    // Given
    when(h2TaskRepository.save(expectedTask)).thenReturn(expectedTask);

    // When
    TaskEntity result = transactionManagementService.executeInH2(repository -> repository.save(expectedTask));

    // Then
    verify(h2TaskRepository, times(1)).save(expectedTask);
    assertEquals(expectedTask, result);
  }
  @Test
  void executeInPostgres_shouldExecuteActionWithPostgresRepository() {
    // Given
    TaskEntity expectedTask = new TaskEntity();
    expectedTask.setId(2L);
    when(postgresTaskRepository.save(expectedTask)).thenReturn(expectedTask);

    // When
    TaskEntity result = transactionManagementService.executeInPostgres(repository -> repository.save(expectedTask));

    // Then
    verify(postgresTaskRepository, times(1)).save(expectedTask);
    assertEquals(expectedTask, result);
  }

  @Test
  void executeInH2WithoutResult_shouldExecuteVoidActionWithH2Repository() {
    // Given
    TaskEntity taskToDelete = new TaskEntity();
    taskToDelete.setId(1L);
    doNothing().when(h2TaskRepository).deleteById(taskToDelete.getId());

    // When
    transactionManagementService.executeInH2WithoutResult(repository -> repository.deleteById(taskToDelete.getId()));

    // Then
    verify(h2TaskRepository, times(1)).deleteById(taskToDelete.getId());
  }

  @Test
  void executeInPostgresWithoutResult_shouldExecuteVoidActionWithPostgresRepository() {
    // Given
    TaskEntity taskToDelete = new TaskEntity();
    taskToDelete.setId(2L);
    doNothing().when(postgresTaskRepository).deleteById(taskToDelete.getId());

    // When
    transactionManagementService.executeInPostgresWithoutResult(repository -> repository.deleteById(taskToDelete.getId()));

    // Then
    verify(postgresTaskRepository, times(1)).deleteById(taskToDelete.getId());
  }
}