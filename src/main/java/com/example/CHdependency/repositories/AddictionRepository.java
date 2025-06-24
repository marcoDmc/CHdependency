package com.example.CHdependency.repositories;

import com.example.CHdependency.models.Addiction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddictionRepository extends JpaRepository<Addiction, Long> {
}
