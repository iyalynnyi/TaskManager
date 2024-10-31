package com.iyalynnyi.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

  private Long id;
  @NotBlank(message = "Title should not be blank.")
  private String title;
  private String description;
  @NotNull(message = "Status should not be null.")
  private TaskStatus status;
  @NotNull(message = "Priority should not be null.")
  private TaskPriority priority;
  private LocalDateTime createdDate;
  private LocalDateTime dueDate;
  private String assignee;
  private String reporter;
}
