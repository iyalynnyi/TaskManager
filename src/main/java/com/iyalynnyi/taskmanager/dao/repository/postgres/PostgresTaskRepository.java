package com.iyalynnyi.taskmanager.dao.repository.postgres;

import com.iyalynnyi.taskmanager.dao.model.TaskEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostgresTaskRepository extends JpaRepository<TaskEntity, Long> {
}
