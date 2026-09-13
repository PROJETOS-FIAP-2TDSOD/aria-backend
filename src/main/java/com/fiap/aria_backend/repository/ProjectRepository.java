package com.fiap.aria_backend.repository;

import com.fiap.aria_backend.model.Project;
import com.fiap.aria_backend.model.ProjectStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findByManagerId(String managerId);
    List<Project> findByStatus(ProjectStatus status);
}