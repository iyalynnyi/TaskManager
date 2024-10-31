package com.iyalynnyi.taskmanager.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.iyalynnyi.taskmanager.dto.TaskDto;
import com.iyalynnyi.taskmanager.dto.TaskStatus;
import com.iyalynnyi.taskmanager.service.TaskService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

class TaskControllerTest {
  private MockMvc mockMvc;

  private TaskService taskService;

  private TaskController taskController;

  @BeforeEach
  void setUp() {
    taskService = Mockito.mock(TaskService.class);
    taskController = new TaskController(taskService);
    mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
  }

  @Test
  void getAll_shouldReturnListOfTasks() throws Exception {
    // Given
    List<TaskDto> tasks = Arrays.asList(
        TaskDto.builder().id(1L).title("Task 1").build(),
        TaskDto.builder().id(2L).title("Task 2").build()
    );
    when(taskService.getTasks()).thenReturn(tasks);

    // When & Then
    mockMvc.perform(get("/api/v1/tasks")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].title").value("Task 1"))
        .andExpect(jsonPath("$[1].id").value(2L))
        .andExpect(jsonPath("$[1].title").value("Task 2"));

    verify(taskService, times(1)).getTasks();
  }

  @Test
  void createTask_shouldReturnCreatedTaskId() throws Exception {
    // Given
    Long taskId = 1L;
    when(taskService.createTask(any(TaskDto.class))).thenReturn(taskId);

    // When & Then
    mockMvc.perform(post("/api/v1/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                        "title": "Implement authentication",
                        "description": "Add JWT-based authentication to the application.",
                        "status": "IN_PROGRESS",
                        "priority": "HIGH",
                        "createdDate": "2024-10-01T10:00:00",
                        "dueDate": "2024-10-15T10:00:00",
                        "assignee": "user1",
                        "reporter": "admin",
                        "tags": ["authentication", "backend"]
                    }
                """))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "http://localhost/api/v1/tasks/1"));

    verify(taskService, times(1)).createTask(any(TaskDto.class));
  }

  @Test
  void deleteTask_shouldReturnOk() throws Exception {
    // Given
    Long taskId = 1L;
    when(taskService.deleteTaskById(taskId)).thenReturn("Task deleted");

    // When & Then
    mockMvc.perform(delete("/api/v1/tasks/{id}", taskId))
        .andExpect(status().isOk())
        .andExpect(content().string("Task deleted"));

    verify(taskService, times(1)).deleteTaskById(taskId);
  }

  @Test
  void updateTask_shouldReturnOk() throws Exception {
    // Given
    Long taskId = 1L;
    when(taskService.updateTask(any(TaskDto.class))).thenReturn("Task updated");

    // When & Then
    mockMvc.perform(patch("/api/v1/tasks/{id}", taskId)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                        "title": "Implement authentication",
                        "description": "Add JWT-based authentication to the application.",
                        "status": "IN_PROGRESS",
                        "priority": "HIGH",
                        "createdDate": "2024-10-01T10:00:00",
                        "dueDate": "2024-10-15T10:00:00",
                        "assignee": "user1",
                        "reporter": "admin",
                        "tags": ["authentication", "backend"]
                    }
                """)).andExpect(status().isOk())
        .andExpect(content().string("Task updated"));

    verify(taskService, times(1)).updateTask(any(TaskDto.class));
  }

  @Test
  void updateStatus_shouldReturnOk() throws Exception {
    // Given
    Long taskId = 1L;
    TaskStatus newStatus = TaskStatus.IN_PROGRESS; // предположим, что у вас есть статус IN_PROGRESS
    when(taskService.updateStatus(taskId, newStatus)).thenReturn("Task status updated");

    // When & Then
    mockMvc.perform(put("/api/v1/tasks/{id}", taskId)
            .param("status", newStatus.name()))
        .andExpect(status().isOk())
        .andExpect(content().string("Task status updated"));

    verify(taskService, times(1)).updateStatus(taskId, newStatus);
  }
}