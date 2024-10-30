package com.iyalynnyi.taskmanager.service.impl;

import com.iyalynnyi.taskmanager.dao.repository.h2.H2TaskRepository;
import com.iyalynnyi.taskmanager.dao.repository.postgres.PostgresTaskRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TaskTransactionManagementService {

  private final H2TaskRepository h2TaskRepository;
  private final PostgresTaskRepository postgresTaskRepository;

  @Transactional(transactionManager = "h2TransactionManager")
  public <T> T executeInH2(H2Action<T> action) {
    return action.perform(h2TaskRepository);
  }

  @Transactional(transactionManager = "postgresTransactionManager")
  public <T> T executeInPostgres(PostgresAction<T> action) {
    return action.perform(postgresTaskRepository);
  }

  @Transactional(transactionManager = "h2TransactionManager")
  public void executeInH2WithoutResult(H2VoidAction action) {
    action.perform(h2TaskRepository);
  }

  @Transactional(transactionManager = "postgresTransactionManager")
  public void executeInPostgresWithoutResult(PostgresVoidAction action) {
    action.perform(postgresTaskRepository);
  }

  public interface H2Action<T> {
    T perform(H2TaskRepository repository);
  }

  public interface PostgresAction<T> {
    T perform(PostgresTaskRepository repository);
  }

  public interface H2VoidAction {
    void perform(H2TaskRepository repository);
  }

  public interface PostgresVoidAction {
    void perform(PostgresTaskRepository repository);
  }
}
