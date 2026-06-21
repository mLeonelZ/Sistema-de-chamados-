package com.helpdesk.repository;

import com.helpdesk.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.slaPolicy")
    List<Category> findAllWithSlaPolicy();

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.slaPolicy WHERE c.id = :id")
    Optional<Category> findByIdWithSlaPolicy(@Param("id") UUID id);
}
