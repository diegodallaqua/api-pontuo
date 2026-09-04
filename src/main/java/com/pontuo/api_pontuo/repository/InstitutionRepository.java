package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.Institution;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {

    @Override
    @EntityGraph(attributePaths = "address")
    List<Institution> findAll();

    @Override
    @EntityGraph(attributePaths = "address")
    Optional<Institution> findById(Long id);

    @EntityGraph(attributePaths = "address")
    List<Institution> findByAcronym(String acronym);
}
