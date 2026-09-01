package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.KnowledgeArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KnowledgeAreaRepository extends JpaRepository<KnowledgeArea, Long> {
}