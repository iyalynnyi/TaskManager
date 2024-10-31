package com.iyalynnyi.taskmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.iyalynnyi.taskmanager.dto.TaskDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

class TaskKafkaProducerTest {

  private KafkaTemplate<String, TaskDto> kafkaTemplate;

  private TaskKafkaProducer taskKafkaProducer;

  @BeforeEach
  void setUp() {
    kafkaTemplate = Mockito.mock(KafkaTemplate.class);
    taskKafkaProducer = new TaskKafkaProducer(kafkaTemplate);
  }

  @Test
  void sendTask_shouldSendTaskDtoToKafkaTopic() {
    // Given
    TaskDto taskDto = TaskDto.builder()
        .title("Sample Title")
        .description("Sample Description")
        .build();

    // When
    taskKafkaProducer.sendTask(taskDto);

    // Then
    verify(kafkaTemplate).send(eq("tasks_topic"), eq(taskDto));
    assertThat(taskDto.getTitle()).isEqualTo("Sample Title");
    assertThat(taskDto.getDescription()).isEqualTo("Sample Description");
  }
}