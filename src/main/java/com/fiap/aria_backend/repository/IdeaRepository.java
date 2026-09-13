package com.fiap.aria_backend.repository;

import com.fiap.aria_backend.model.Idea;
import com.fiap.aria_backend.model.IdeaStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IdeaRepository extends MongoRepository<Idea, String> {
    List<Idea> findByAuthorId(String authorId);
    List<Idea> findByStatus(IdeaStatus status);
}