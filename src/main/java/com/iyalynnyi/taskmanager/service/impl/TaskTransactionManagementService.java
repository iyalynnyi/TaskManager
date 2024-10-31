package com.iyalynnyi.taskmanager.service.impl;

import com.iyalynnyi.taskmanager.dao.repository.h2.H2TaskRepository;
import com.iyalynnyi.taskmanager.dao.repository.postgres.PostgresTaskRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing database operations across H2 and PostgreSQL repositories
 * with specific transaction management per database.
 */
@Service
@RequiredArgsConstructor
public class TaskTransactionManagementService {

  private final H2TaskRepository h2TaskRepository;
  private final PostgresTaskRepository postgresTaskRepository;

  /**
   * Executes an action within an H2 transaction context.
   *
   * @param action the action to perform on the H2 repository
   * @param <T> the type of result returned by the action
   * @return the result of the action
   */
  @Transactional(transactionManager = "h2TransactionManager")
  public <T> T executeInH2(H2Action<T> action) {
    return action.perform(h2TaskRepository);
  }

  /**
   * Executes an action within a PostgreSQL transaction context.
   *
   * @param action the action to perform on the PostgreSQL repository
   * @param <T> the type of result returned by the action
   * @return the result of the action
   */
  @Transactional(transactionManager = "postgresTransactionManager")
  public <T> T executeInPostgres(PostgresAction<T> action) {
    return action.perform(postgresTaskRepository);
  }

  /**
   * Executes a void action within an H2 transaction context.
   *
   * @param action the void action to perform on the H2 repository
   */
  @Transactional(transactionManager = "h2TransactionManager")
  public void executeInH2WithoutResult(H2VoidAction action) {
    action.perform(h2TaskRepository);
  }

  /**
   * Executes a void action within a PostgreSQL transaction context.
   *
   * @param action the void action to perform on the PostgreSQL repository
   */
  @Transactional(transactionManager = "postgresTransactionManager")
  public void executeInPostgresWithoutResult(PostgresVoidAction action) {
    action.perform(postgresTaskRepository);
  }

  /**
   * Functional interface for executing an action on the H2 repository.
   *
   * @param <T> the type of result returned by the action
   */
  public interface H2Action<T> {
    T perform(H2TaskRepository repository);
  }

  /**
   * Functional interface for executing an action on the PostgreSQL repository.
   *
   * @param <T> the type of result returned by the action
   */
  public interface PostgresAction<T> {
    T perform(PostgresTaskRepository repository);
  }

  /**
   * Functional interface for executing a void action on the H2 repository.
   */
  public interface H2VoidAction {
    void perform(H2TaskRepository repository);
  }

  /**
   * Functional interface for executing a void action on the PostgreSQL repository.
   */
  public interface PostgresVoidAction {
    void perform(PostgresTaskRepository repository);
  }
}
