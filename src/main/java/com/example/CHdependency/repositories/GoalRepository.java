package com.example.CHdependency.repositories;

import com.example.CHdependency.entities.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    Goal findByName(String name);
    Goal findByUserId(Long id);
}
