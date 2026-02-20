package com.example.backend.repository;

import com.example.backend.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    Optional<Area> findByExternalId(Integer externalId);
    Optional<Area> findByName(String name);
    boolean existsByName(String name);
}
