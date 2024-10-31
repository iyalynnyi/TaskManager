package com.iyalynnyi.taskmanager.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.iyalynnyi.taskmanager.dao.model.TaskEntity;
import com.iyalynnyi.taskmanager.exception.ApiResponseException;
import com.iyalynnyi.taskmanager.service.TaskTransactionManagementService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

class TaskRepositoryManagementServiceImplTest {

  private TaskTransactionManagementService transactionManagementService;
  private TaskRepositoryManagementServiceImpl taskRepositoryManagementService;

  @BeforeEach
  void setUp() {
    transactionManagementService = Mockito.mock(TaskTransactionManagementService.class);
    taskRepositoryManagementService = new TaskRepositoryManagementServiceImpl(transactionManagementService);
  }

  @Test
  public void saveWithFallback_shouldSaveInH2() {
    // Given
    TaskEntity task = new TaskEntity();
    when(transactionManagementService.executeInH2(any())).thenReturn(task);

    // When
    TaskEntity result = taskRepositoryManagementService.saveWithFallback(task);

    // Then
    verify(transactionManagementService).executeInH2(any());
    assertEquals(task, result);
  }

  @Test
  public void saveWithFallback_shouldFallbackToPostgresWhenH2Fails() {
    // Given
    TaskEntity task = new TaskEntity();
    when(transactionManagementService.executeInH2(any())).thenThrow(new RuntimeException("H2 error"));
    when(transactionManagementService.executeInPostgres(any())).thenReturn(task);

    // When
    TaskEntity result = taskRepositoryManagementService.saveWithFallback(task);

    // Then
    verify(transactionManagementService).executeInH2(any());
    verify(transactionManagementService).executeInPostgres(any());
    assertEquals(task, result);
  }

  @Test
  public void findByIdWithFallback_shouldFindInH2() {
    // Given
    TaskEntity task = new TaskEntity();
    when(transactionManagementService.executeInH2(any())).thenReturn(Optional.of(task));

    // When
    TaskEntity result = taskRepositoryManagementService.findByIdWithFallback(1L);

    // Then
    verify(transactionManagementService).executeInH2(any());
    assertEquals(task, result);
  }

  @Test
  public void findByIdWithFallback_shouldFallbackToPostgresWhenH2Fails() {
    // Given
    when(transactionManagementService.executeInH2(any())).thenThrow(new RuntimeException("H2 error"));
    when(transactionManagementService.executeInPostgres(any())).thenReturn(Optional.of(new TaskEntity()));

    // When
    TaskEntity result = taskRepositoryManagementService.findByIdWithFallback(1L);

    // Then
    verify(transactionManagementService).executeInH2(any());
    verify(transactionManagementService).executeInPostgres(any());
    assertNotNull(result);
  }

  @Test
  public void findByIdWithFallback_shouldThrowExceptionWhenNotFoundInBothDatabases() {
    // Given
    when(transactionManagementService.executeInH2(any())).thenReturn(Optional.empty());
    when(transactionManagementService.executeInPostgres(any())).thenReturn(Optional.empty());

    // When & Then
    ApiResponseException exception = assertThrows(ApiResponseException.class, () -> taskRepositoryManagementService.findByIdWithFallback(1L));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  public void findAllWithFallback_shouldRetrieveFromBothDatabases() {
    // Given
    TaskEntity taskInH2 = new TaskEntity();
    TaskEntity taskInPostgres = new TaskEntity();
    when(transactionManagementService.executeInH2(any())).thenReturn(Collections.singletonList(taskInH2));
    when(transactionManagementService.executeInPostgres(any())).thenReturn(Collections.singletonList(taskInPostgres));

    // When
    List<TaskEntity> results = taskRepositoryManagementService.findAllWithFallback();

    // Then
    verify(transactionManagementService).executeInH2(any());
    verify(transactionManagementService).executeInPostgres(any());
    assertEquals(2, results.size());
  }

  @Test
  public void findAllWithFallback_shouldRetrieveFromPostgresWhenH2Fails() {
    // Given
    TaskEntity taskInPostgres = new TaskEntity();
    when(transactionManagementService.executeInH2(any())).thenThrow(new RuntimeException("H2 error"));
    when(transactionManagementService.executeInPostgres(any())).thenReturn(Collections.singletonList(taskInPostgres));

    // When
    List<TaskEntity> results = taskRepositoryManagementService.findAllWithFallback();

    // Then
    verify(transactionManagementService).executeInH2(any());
    verify(transactionManagementService).executeInPostgres(any());
    assertEquals(1, results.size());
  }

  @Test
  public void existsById_shouldReturnTrueIfExistsInH2() {
    // Given
    when(transactionManagementService.executeInH2(any())).thenReturn(true);

    // When
    boolean exists = taskRepositoryManagementService.existsById(1L);

    // Then
    verify(transactionManagementService).executeInH2(any());
    assertTrue(exists);
  }

  @Test
  public void existsById_shouldFallbackToPostgresWhenH2Fails() {
    // Given
    when(transactionManagementService.executeInH2(any())).thenThrow(new RuntimeException("H2 error"));
    when(transactionManagementService.executeInPostgres(any())).thenReturn(true);

    // When
    boolean exists = taskRepositoryManagementService.existsById(1L);

    // Then
    verify(transactionManagementService).executeInH2(any());
    verify(transactionManagementService).executeInPostgres(any());
    assertTrue(exists);
  }

  @Test
  public void deleteById_shouldDeleteInH2() {
    // Given
    doNothing().when(transactionManagementService).executeInH2WithoutResult(any());

    // When
    taskRepositoryManagementService.deleteById(1L);

    // Then
    verify(transactionManagementService).executeInH2WithoutResult(any());
  }

  @Test
  public void deleteById_shouldFallbackToPostgresWhenH2Fails() {
    // Given
    doThrow(new RuntimeException("H2 error")).when(transactionManagementService).executeInH2WithoutResult(any());
    doNothing().when(transactionManagementService).executeInPostgresWithoutResult(any());

    // When
    taskRepositoryManagementService.deleteById(1L);

    // Then
    verify(transactionManagementService).executeInH2WithoutResult(any());
    verify(transactionManagementService).executeInPostgresWithoutResult(any());
  }
}