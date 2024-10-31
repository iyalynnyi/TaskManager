package com.iyalynnyi.taskmanager.service.impl;

import static com.iyalynnyi.taskmanager.dto.TaskPriority.HIGH;
import static com.iyalynnyi.taskmanager.dto.TaskStatus.DONE;
import static com.iyalynnyi.taskmanager.dto.TaskStatus.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.iyalynnyi.taskmanager.converter.TaskConverter;
import com.iyalynnyi.taskmanager.dao.model.TaskEntity;
import com.iyalynnyi.taskmanager.dto.TaskDto;
import com.iyalynnyi.taskmanager.dto.TaskStatus;
import com.iyalynnyi.taskmanager.exception.ApiResponseException;
import com.iyalynnyi.taskmanager.service.TaskKafkaProducer;
import com.iyalynnyi.taskmanager.service.TaskService;
import com.iyalynnyi.taskmanager.service.TaskTransactionManagementService;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

class TaskServiceImplTest {

  private TaskService taskService;
  private TaskConverter taskConverter;
  private TaskKafkaProducer kafkaProducer;
  private TaskRepositoryManagementServiceImpl taskRepositoryManagementService;

  @BeforeEach
  void setUp() {
    taskRepositoryManagementService = Mockito.mock(TaskRepositoryManagementServiceImpl.class);
    taskConverter = Mockito.mock(TaskConverter.class);
    kafkaProducer = Mockito.mock(TaskKafkaProducer.class);

    taskService = new TaskServiceImpl(taskRepositoryManagementService, taskConverter, kafkaProducer);

  }

  @Test
  public void shouldCreateTask() {
    //Given
    LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
    TaskDto expectedTaskDto = TaskDto.builder()
        .title("Implement authentication")
        .description("Add JWT-based authentication to the application.")
        .assignee("user1")
        .reporter("admin")
        .priority(HIGH)
        .status(IN_PROGRESS)
        .dueDate(dueDate)
        .build();
    //When
    TaskEntity taskEntity = new TaskEntity();
    taskEntity.setId(1L);
    when(taskConverter.toEntity(expectedTaskDto)).thenReturn(taskEntity);
    when(taskRepositoryManagementService.saveWithFallback(any(TaskEntity.class))).thenReturn(taskEntity);
    when(taskConverter.toDto(taskEntity)).thenReturn(expectedTaskDto);

    Long createdTaskId = taskService.createTask(expectedTaskDto);

    //Then
    verify(taskConverter).toEntity(expectedTaskDto);
    verify(taskRepositoryManagementService).saveWithFallback(taskEntity);
    verify(kafkaProducer).sendTask(expectedTaskDto);
    assertEquals(1L, createdTaskId);
  }

  @Test
  public void shouldUpdateTask() {
    // Given
    TaskDto taskDto = TaskDto.builder().id(1L).title("Updated Task").build();
    TaskEntity existingTaskEntity = new TaskEntity();
    existingTaskEntity.setId(1L);
    existingTaskEntity.setTitle("Old Task");

    when(taskRepositoryManagementService.findByIdWithFallback(taskDto.getId())).thenReturn(existingTaskEntity);
    when(taskConverter.updateEntity(existingTaskEntity, taskDto)).thenReturn(existingTaskEntity);

    // When
    String response = taskService.updateTask(taskDto);

    // Then
    verify(taskRepositoryManagementService).findByIdWithFallback(taskDto.getId());
    verify(taskConverter).updateEntity(existingTaskEntity, taskDto);
    verify(taskRepositoryManagementService).saveWithFallback(existingTaskEntity);
    assertEquals("Task updated successfully.", response);
  }

  @Test
  public void shouldUpdateTaskStatus() {
    // Given
    Long taskId = 1L;
    TaskStatus newStatus = TaskStatus.DONE;
    TaskEntity existingTaskEntity = new TaskEntity();
    existingTaskEntity.setId(taskId);
    existingTaskEntity.setStatus(TaskStatus.IN_PROGRESS);

    when(taskRepositoryManagementService.findByIdWithFallback(taskId)).thenReturn(existingTaskEntity);

    // When
    String response = taskService.updateStatus(taskId, newStatus);

    // Then
    verify(taskRepositoryManagementService).findByIdWithFallback(taskId);
    assertEquals(newStatus, existingTaskEntity.getStatus());
    verify(taskRepositoryManagementService).saveWithFallback(existingTaskEntity);
    assertEquals("Status updated successfully.", response);
  }

  @Test
  public void shouldGetTasks() {
    // Given
    TaskEntity taskEntity = new TaskEntity();
    taskEntity.setId(1L);
    List<TaskEntity> taskEntities = Collections.singletonList(taskEntity);
    when(taskRepositoryManagementService.findAllWithFallback()).thenReturn(taskEntities);
    when(taskConverter.toDtos(taskEntities)).thenReturn(Collections.singletonList(TaskDto.builder().id(1L).build()));

    // When
    List<TaskDto> tasks = taskService.getTasks();

    // Then
    verify(taskRepositoryManagementService).findAllWithFallback();
    assertEquals(1, tasks.size());
    assertEquals(1L, tasks.get(0).getId());
  }

  @Test
  public void shouldDeleteTaskById() {
    // Given
    Long taskId = 1L;
    when(taskRepositoryManagementService.existsById(taskId)).thenReturn(true);

    // When
    String response = taskService.deleteTaskById(taskId);

    // Then
    verify(taskRepositoryManagementService).existsById(taskId);
    verify(taskRepositoryManagementService).deleteById(taskId);
    assertEquals("Task deleted successfully.", response);
  }

  @Test
  public void shouldFailToDeleteTaskWhenNotFound() {
    // Given
    Long taskId = 1L;
    when(taskRepositoryManagementService.existsById(taskId)).thenReturn(false);

    // When & Then
    assertThrows(ApiResponseException.class, () -> taskService.deleteTaskById(taskId));
    verify(taskRepositoryManagementService).existsById(taskId);
    verify(taskRepositoryManagementService, never()).deleteById(taskId);
  }
}