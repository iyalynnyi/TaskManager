package com.iyalynnyi.taskmanager.dao.repository.h2;

import com.iyalynnyi.taskmanager.dao.model.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface H2TaskRepository extends JpaRepository<TaskEntity, Long> {
}
