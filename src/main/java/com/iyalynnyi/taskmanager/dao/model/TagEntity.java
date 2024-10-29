package com.iyalynnyi.taskmanager.dao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;


@Getter
@Setter
@Entity
@Table(name = "tag")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, length = 50)
  private String name;

  @ManyToMany(mappedBy = "tags")
  private List<TaskEntity> tasks;
}
