package com.iyalynnyi.taskmanager.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.iyalynnyi.taskmanager.dto.TaskDto;
import com.iyalynnyi.taskmanager.dto.TaskStatus;
import com.iyalynnyi.taskmanager.service.TaskService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = APPLICATION_JSON_VALUE, path = "api/v1/tasks")
public class TaskController {

  private final TaskService taskService;

  /**
   * Retrieves all tasks.
   *
   * @return a ResponseEntity containing a list of TaskDto objects
   */
  @GetMapping
  public ResponseEntity<List<TaskDto>> getAll() {
    return ResponseEntity.ok(taskService.getTasks());
  }

  /**
   * Creates a new task.
   *
   * @param taskDto the task data transfer object containing task details
   * @return a ResponseEntity containing the ID of the created task and a location header pointing to the created resource
   */
  @PostMapping
  public ResponseEntity<Long> createTask(@Valid @RequestBody TaskDto taskDto) {
    Long taskId = taskService.createTask(taskDto);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(taskId)
        .toUri();
    return ResponseEntity.created(location).body(taskId);
  }

  /**
   * Deletes a task by its ID.
   *
   * @param id the ID of the task to delete
   * @return a ResponseEntity containing a success message
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteTask(@PathVariable Long id) {
    return ResponseEntity.ok(taskService.deleteTaskById(id));
  }

  /**
   * Updates an existing task.
   *
   * @param id      the ID of the task to update
   * @param taskDto the task data transfer object containing updated task details
   * @return a ResponseEntity containing a success message
   */
  @PatchMapping("/{id}")
  public ResponseEntity<String> updateTask(@PathVariable(required = true) Long id,
      @Valid @RequestBody TaskDto taskDto) {
    taskDto.setId(id);
    return ResponseEntity.ok(taskService.updateTask(taskDto));
  }

  /**
   * Updates the status of a task by its ID.
   *
   * @param id         the ID of the task to update
   * @param taskStatus the new status to set for the task
   * @return a ResponseEntity containing a success message
   */
  @PutMapping("/{id}")
  public ResponseEntity<String> updateStatus(@PathVariable(required = true) Long id,
      @Valid @RequestParam(name = "status", required = true) TaskStatus taskStatus) {
    return ResponseEntity.ok(taskService.updateStatus(id, taskStatus));
  }
}

