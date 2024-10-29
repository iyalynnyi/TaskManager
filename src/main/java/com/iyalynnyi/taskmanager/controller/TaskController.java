package com.iyalynnyi.taskmanager.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.iyalynnyi.taskmanager.dto.TaskDto;
import com.iyalynnyi.taskmanager.dto.TaskStatus;
import com.iyalynnyi.taskmanager.service.TaskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = APPLICATION_JSON_VALUE, path = "api/v1/tasks")
public class TaskController {

  private final TaskService taskService;

  @GetMapping
  public ResponseEntity<List<TaskDto>> getAll() {
    return ResponseEntity.ok(taskService.getTasks());
  }

  @PostMapping
  public ResponseEntity<Long> createTask(@Valid @RequestBody TaskDto taskDto) {
    Long taskId = taskService.createTask(taskDto);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(taskId)
        .toUri();
    return ResponseEntity.created(location).body(taskId);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteTask(@PathVariable Long id) {
    return ResponseEntity.ok(taskService.deleteTaskById(id));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<String> updateTask(@PathVariable(required = true) Long id, @Valid @RequestBody TaskDto taskDto) {
    taskDto.setId(id);
    return ResponseEntity.ok(taskService.updateTask(taskDto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<String> updateStatus(@PathVariable(required = true) Long id, @Valid() @RequestParam(name = "status", required = true) TaskStatus taskDto) {
    return ResponseEntity.ok(taskService.updateStatus(id, taskDto));
  }
}
