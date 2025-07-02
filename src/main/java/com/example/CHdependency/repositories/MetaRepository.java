package com.example.CHdependency.repositories;

import com.example.CHdependency.entities.Metas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetaRepository extends JpaRepository<Metas, Long> {
}
