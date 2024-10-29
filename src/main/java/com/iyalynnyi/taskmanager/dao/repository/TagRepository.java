package com.iyalynnyi.taskmanager.dao.repository;

import com.iyalynnyi.taskmanager.dao.model.TagEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {

  @Query("SELECT t FROM TagEntity t WHERE t.name IN :names")
  List<TagEntity> findAllByNameIn(List<String> names);
}
