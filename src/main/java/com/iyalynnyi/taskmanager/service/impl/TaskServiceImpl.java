package com.iyalynnyi.taskmanager.service.impl;

import com.iyalynnyi.taskmanager.converter.TaskConverter;
import com.iyalynnyi.taskmanager.dao.model.TaskEntity;
import com.iyalynnyi.taskmanager.dto.TaskDto;
import com.iyalynnyi.taskmanager.dto.TaskStatus;
import com.iyalynnyi.taskmanager.exception.ApiResponseException;
import com.iyalynnyi.taskmanager.service.TaskKafkaProducer;
import com.iyalynnyi.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

  private final TaskRepositoryManagementServiceImpl taskRepositoryManagementService;
  private final TaskConverter taskConverter;
  private final TaskKafkaProducer taskKafkaProducer;

  /**
   * Creates a new task.
   *
   * @param taskDto the task data transfer object containing task details
   * @return the ID of the created task
   * @throws ApiResponseException if there is an error during task creation
   */
  @Override
  public Long createTask(TaskDto taskDto) {
    log.trace("Creating task: {}", taskDto);
    TaskEntity entity = taskConverter.toEntity(taskDto);
    taskRepositoryManagementService.saveWithFallback(entity);
    log.trace("Created task with id: {}", entity.getId());
    taskKafkaProducer.sendTask(taskConverter.toDto(entity));
    return entity.getId();
  }

  /**
   * Updates an existing task.
   *
   * @param taskDto the task data transfer object containing updated task details
   * @return a success message indicating the task was updated
   * @throws ApiResponseException if the task ID is null or not found
   */
  @Override
  public String updateTask(TaskDto taskDto) {
    log.trace("Updating task: {}", taskDto);
    if (taskDto.getId() == null) {
      throw new ApiResponseException("Task Id is required to update the task!", HttpStatus.BAD_REQUEST);
    }
    TaskEntity taskEntity = taskRepositoryManagementService.findByIdWithFallback(taskDto.getId());
    TaskEntity updatedEntity = taskConverter.updateEntity(taskEntity, taskDto);
    taskRepositoryManagementService.saveWithFallback(updatedEntity);
    log.trace("Updated task with id: {}", updatedEntity.getId());
    return "Task updated successfully.";
  }

  /**
   * Updates the status of a specific task.
   *
   * @param id the ID of the task to be updated
   * @param taskStatus the new status for the task
   * @return a success message indicating the status was updated
   * @throws ApiResponseException if the task is not found
   */
  @Override
  public String updateStatus(Long id, TaskStatus taskStatus) {
    log.trace("Updating status of task with id: {}", id);
    TaskEntity taskEntity = taskRepositoryManagementService.findByIdWithFallback(id);
    taskEntity.setStatus(taskStatus);
    taskRepositoryManagementService.saveWithFallback(taskEntity);
    log.trace("Updated status of task with id: {}", taskEntity.getId());
    return "Status updated successfully.";
  }

  /**
   * Retrieves all tasks.
   *
   * @return a list of tasks or an empty list
   */
  @Override
  public List<TaskDto> getTasks() {
    log.trace("Retrieving all tasks");
    return taskConverter.toDtos(taskRepositoryManagementService.findAllWithFallback());
  }

  /**
   * Deletes a task by its ID.
   *
   * @param id the ID of the task to be deleted
   * @return a success message indicating the task was deleted
   * @throws ApiResponseException if the task is not found
   */
  @Override
  public String deleteTaskById(Long id) {
    log.trace("Deleting task with id: {}", id);
    if (!taskRepositoryManagementService.existsById(id)) {
      throw new ApiResponseException("Task not found!", HttpStatus.NOT_FOUND);
    }
    taskRepositoryManagementService.deleteById(id);
    log.trace("Deleted task with id: {}", id);
    return "Task deleted successfully.";
  }

}
