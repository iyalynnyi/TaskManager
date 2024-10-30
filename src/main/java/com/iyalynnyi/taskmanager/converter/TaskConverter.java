package com.iyalynnyi.taskmanager.converter;

import com.iyalynnyi.taskmanager.dao.model.TaskEntity;
import com.iyalynnyi.taskmanager.dto.TaskDto;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
public class TaskConverter {

  public TaskEntity toEntity(TaskDto task) {
    return TaskEntity.builder()
        .title(task.getTitle())
        .description(task.getDescription())
        .assignee(task.getAssignee())
        .reporter(task.getReporter())
        .priority(task.getPriority())
        .status(task.getStatus())
        .createdDate(LocalDateTime.now())
        .dueDate(task.getDueDate())
        .build();
  }

  public TaskDto toDto(TaskEntity task) {
    if (task == null) {
      return null;
    }
    return TaskDto.builder()
        .id(task.getId())
        .title(task.getTitle())
        .description(task.getDescription())
        .assignee(task.getAssignee())
        .reporter(task.getReporter())
        .priority(task.getPriority())
        .status(task.getStatus())
        .createdDate(LocalDateTime.now())
        .dueDate(task.getDueDate())
        .build();
  }

  public List<TaskDto> toDtos(List<TaskEntity> tasks) {
    if (tasks == null || tasks.isEmpty()) {
      return Collections.emptyList();
    }
    return tasks.stream()
        .map(this::toDto)
        .toList();
  }

  public TaskEntity updateEntity(TaskEntity taskEntity, TaskDto task) {
    if (task == null) {
      throw new IllegalArgumentException("Task cannot be null");
    }
    taskEntity.setTitle(task.getTitle());
    taskEntity.setDescription(task.getDescription());
    taskEntity.setAssignee(task.getAssignee());
    taskEntity.setReporter(task.getReporter());
    taskEntity.setPriority(task.getPriority());
    taskEntity.setStatus(task.getStatus());
    taskEntity.setDueDate(task.getDueDate());
    taskEntity.setUpdatedDate(LocalDateTime.now());

    return taskEntity;
  }
}
