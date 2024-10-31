package com.iyalynnyi.taskmanager.service;

import com.iyalynnyi.taskmanager.dto.TaskDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TaskKafkaProducer {

  private final KafkaTemplate<String, TaskDto> kafkaTemplate;
  private static final String TOPIC = "tasks_topic";

  /**
   * Constructs a TaskKafkaProducer with the provided KafkaTemplate.
   *
   * @param kafkaTemplate the KafkaTemplate used to send messages to the Kafka topic.
   */
  public TaskKafkaProducer(KafkaTemplate<String, TaskDto> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  /**
   * Sends a TaskDto message to the specified Kafka topic.
   *
   * @param taskDto the task data transfer object to be sent.
   */
  public void sendTask(TaskDto taskDto) {
    log.info("Sending task to Kafka topic '{}': {}", TOPIC, taskDto);
    kafkaTemplate.send(TOPIC, taskDto);
    log.info("Task sent successfully to Kafka topic '{}'.", TOPIC);
  }
}

