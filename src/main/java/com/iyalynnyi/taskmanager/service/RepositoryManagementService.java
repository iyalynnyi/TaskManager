package com.iyalynnyi.taskmanager.service;

import java.util.List;

public interface RepositoryManagementService <ENTITY> {

  ENTITY saveWithFallback(ENTITY entity);

  ENTITY findByIdWithFallback(Long id);

  List<ENTITY> findAllWithFallback();

  boolean existsById(Long id);

  void deleteById(Long id);


}
