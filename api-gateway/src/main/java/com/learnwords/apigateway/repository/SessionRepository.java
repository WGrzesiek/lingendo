package com.learnwords.apigateway.repository;

import com.learnwords.apigateway.entity.Session;
import org.springframework.data.repository.CrudRepository;

public interface SessionRepository extends CrudRepository<Session, String> {
}
