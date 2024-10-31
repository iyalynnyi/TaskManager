package com.iyalynnyi.taskmanager.converter;

import static com.iyalynnyi.taskmanager.dto.TaskPriority.HIGH;
import static com.iyalynnyi.taskmanager.dto.TaskPriority.LOW;
import static com.iyalynnyi.taskmanager.dto.TaskStatus.IN_PROGRESS;
import static com.iyalynnyi.taskmanager.dto.TaskStatus.TODO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.iyalynnyi.taskmanager.dao.model.TaskEntity;
import com.iyalynnyi.taskmanager.dto.TaskDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

class TaskConverterTest {

  private TaskConverter taskConverter;

  @BeforeEach
  void setUp() {
    taskConverter = new TaskConverter();
  }

  @Test
  void toEntity_shouldConvertTaskDtoToTaskEntity() {
    // Given
    TaskDto taskDto = TaskDto.builder()
        .title("Title")
        .description("Description")
        .assignee("Assignee")
        .reporter("Reporter")
        .priority(HIGH)
        .status(IN_PROGRESS)
        .dueDate(LocalDateTime.now().plusDays(1))
        .build();

    TaskEntity expectedEntity = TaskEntity.builder()
        .title(taskDto.getTitle())
        .description(taskDto.getDescription())
        .assignee(taskDto.getAssignee())
        .reporter(taskDto.getReporter())
        .priority(taskDto.getPriority())
        .status(taskDto.getStatus())
        .dueDate(taskDto.getDueDate())
        .build();
    // When
    TaskEntity taskEntity = taskConverter.toEntity(taskDto);

    // Then
    assertThat(taskEntity).usingRecursiveComparison().ignoringFields("createdDate").isEqualTo(expectedEntity);
    assertThat(taskEntity.getCreatedDate()).isBetween(LocalDateTime.now().minusSeconds(5), LocalDateTime.now());

  }

  @Test
  void toDto_shouldReturnNullWhenTaskEntityIsNull() {
    // When
    TaskDto result = taskConverter.toDto(null);

    // Then
    assertThat(result).isNull();
  }

  @Test
  void toDtos_shouldConvertListOfTaskEntitiesToListOfTaskDtos() {
    // Given
    TaskEntity taskEntity1 = TaskEntity.builder().id(1L).title("Task 1").build();
    TaskEntity taskEntity2 = TaskEntity.builder().id(2L).title("Task 2").build();
    List<TaskEntity> taskEntities = List.of(taskEntity1, taskEntity2);

    TaskDto expectedDto1 = TaskDto.builder().id(taskEntity1.getId()).title(taskEntity1.getTitle()).build();
    TaskDto expectedDto2 = TaskDto.builder().id(taskEntity2.getId()).title(taskEntity2.getTitle()).build();
    List<TaskDto> expectedDtos = List.of(expectedDto1, expectedDto2);

    // When
    List<TaskDto> taskDtos = taskConverter.toDtos(taskEntities);

    // Then
    assertThat(taskDtos).usingRecursiveComparison().ignoringFields("createdDate").isEqualTo(expectedDtos);
  }

  @Test
  void toDtos_shouldReturnEmptyListWhenInputIsNullOrEmpty() {
    // When & Then
    assertThat(taskConverter.toDtos(null)).isEmpty();
    assertThat(taskConverter.toDtos(List.of())).isEmpty();
  }

  @Test
  void updateEntity_shouldUpdateTaskEntityWithTaskDtoValues() {
    // Given
    TaskEntity taskEntity = TaskEntity.builder()
        .id(1L)
        .title("Old Title")
        .description("Old Description")
        .assignee("Old Assignee")
        .reporter("Old Reporter")
        .priority(LOW)
        .status(TODO)
        .dueDate(LocalDateTime.now().minusDays(1))
        .build();

    TaskDto taskDto = TaskDto.builder()
        .title("New Title")
        .description("New Description")
        .assignee("New Assignee")
        .reporter("New Reporter")
        .priority(HIGH)
        .status(IN_PROGRESS)
        .dueDate(LocalDateTime.now().plusDays(1))
        .build();

    TaskEntity expectedEntity = TaskEntity.builder()
        .id(taskEntity.getId())
        .title(taskDto.getTitle())
        .description(taskDto.getDescription())
        .assignee(taskDto.getAssignee())
        .reporter(taskDto.getReporter())
        .priority(taskDto.getPriority())
        .status(taskDto.getStatus())
        .dueDate(taskDto.getDueDate())
        .build();

    // When
    TaskEntity updatedEntity = taskConverter.updateEntity(taskEntity, taskDto);

    // Then
    assertThat(updatedEntity).usingRecursiveComparison().ignoringFields("updatedDate").isEqualTo(expectedEntity);
    assertThat(updatedEntity.getUpdatedDate()).isBetween(LocalDateTime.now().minusSeconds(5), LocalDateTime.now());
  }

  @Test
  void updateEntity_shouldThrowExceptionWhenTaskDtoIsNull() {
    // Given
    TaskEntity taskEntity = new TaskEntity();

    // Then
    assertThrows(IllegalArgumentException.class, () -> taskConverter.updateEntity(taskEntity, null));
  }
}