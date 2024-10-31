package com.iyalynnyi.taskmanager.service.impl;

import com.iyalynnyi.taskmanager.dto.TaskDto;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskKafkaProducer {

  private final KafkaTemplate<String, TaskDto> kafkaTemplate;
  private static final String TOPIC = "tasks_topic";

  public TaskKafkaProducer(KafkaTemplate<String, TaskDto> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendTask(TaskDto taskDto) {
    kafkaTemplate.send(TOPIC, taskDto);
  }
}
