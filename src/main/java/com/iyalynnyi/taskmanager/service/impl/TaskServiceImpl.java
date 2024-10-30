package com.iyalynnyi.taskmanager.service.impl;

import com.iyalynnyi.taskmanager.converter.TaskConverter;
import com.iyalynnyi.taskmanager.dao.model.TagEntity;
import com.iyalynnyi.taskmanager.dao.model.TaskEntity;
import com.iyalynnyi.taskmanager.dao.repository.TagRepository;
import com.iyalynnyi.taskmanager.dao.repository.TaskRepository;
import com.iyalynnyi.taskmanager.dto.TaskDto;
import com.iyalynnyi.taskmanager.dto.TaskStatus;
import com.iyalynnyi.taskmanager.exception.ApiResponseException;
import com.iyalynnyi.taskmanager.service.TaskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

  private final TaskRepository taskRepository;
  private final TagRepository tagRepository;
  private final TaskConverter taskConverter;

  @Override
  public Long createTask(TaskDto taskDto) {
    log.trace("Creating task: {}", taskDto);
    TaskEntity entity = taskConverter.toEntity(taskDto);
    entity.setTags(getExistingTagsOrCreate(taskDto.getTags()));
    taskRepository.save(entity);
    log.trace("Created task with id: {}", entity.getId());
    return entity.getId();
  }

  @Override
  public String updateTask(TaskDto taskDto) {
    log.trace("Updating task: {}", taskDto);
    if (taskDto.getId() == null) {
      throw new ApiResponseException("Task Id is required to update the task!", HttpStatus.BAD_REQUEST);
    }
    TaskEntity taskEntity = taskRepository.findById(taskDto.getId()).orElseThrow(() -> new ApiResponseException("Task not found!", HttpStatus.NOT_FOUND));
    TaskEntity updatedEntity = taskConverter.updateEntity(taskEntity, taskDto);
    taskEntity.setTags(getExistingTagsOrCreate(taskDto.getTags()));
    taskRepository.save(updatedEntity);
    log.trace("Updated task with id: {}", updatedEntity.getId());
    return "Task updated successfully.";
  }

  @Override
  public String updateStatus(Long id, TaskStatus taskStatus) {
    log.trace("Updating status of task with id: {}", id);
    TaskEntity taskEntity = taskRepository.findById(id).orElseThrow(() -> new ApiResponseException("Task not found!", HttpStatus.NOT_FOUND));
    taskEntity.setStatus(taskStatus);
    taskRepository.save(taskEntity);
    log.trace("Updated status of task with id: {}", taskEntity.getId());
    return "Status updated successfully.";
  }

  @Override
  public List<TaskDto> getTasks() {
    log.trace("Retrieving all tasks");
    return taskConverter.toDtos(taskRepository.findAll());
  }

  @Override
  public String deleteTaskById(Long id) {
    log.trace("Deleting task with id: {}", id);
    if (!taskRepository.existsById(id)) {
      throw new ApiResponseException("Task not found!", HttpStatus.NOT_FOUND);
    }
    taskRepository.deleteById(id);
    log.trace("Deleted task with id: {}", id);
    return "Task deleted successfully.";
  }

  private List<TagEntity> getExistingTagsOrCreate(List<String> tags) {
    log.trace("Retrieving existing tags");
    List<TagEntity> existingTags = tagRepository.findAllByNameIn(tags);

    Map<String, TagEntity> tagMap = existingTags.stream()
        .collect(Collectors.toMap(TagEntity::getName, tag -> tag));

    return tags.stream()
        .map(tagName -> tagMap.getOrDefault(tagName, TagEntity.builder().name(tagName).build()))
        .collect(Collectors.toList());
  }
}
