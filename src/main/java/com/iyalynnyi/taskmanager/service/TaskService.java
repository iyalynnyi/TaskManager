package com.iyalynnyi.taskmanager.service;

import com.iyalynnyi.taskmanager.dto.TaskDto;
import com.iyalynnyi.taskmanager.dto.TaskStatus;
import java.util.List;

public interface TaskService {

  Long createTask(TaskDto taskDto);

  String updateTask(TaskDto taskDto);

  String updateStatus (Long id, TaskStatus taskStatus);

  List<TaskDto> getTasks();

  String deleteTaskById(Long id);
}
