package com.fiap.aria_backend.repository;

import com.fiap.aria_backend.model.Orientation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrientationRepository extends MongoRepository<Orientation, String> {
}