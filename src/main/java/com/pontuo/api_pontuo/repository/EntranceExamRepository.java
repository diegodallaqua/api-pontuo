package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.EntranceExam;
import com.pontuo.api_pontuo.entity.Institution;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntranceExamRepository extends JpaRepository<EntranceExam, Long> {

    @Override
    @EntityGraph(attributePaths = "institution")
    List<EntranceExam> findAll();

    @Override
    @EntityGraph(attributePaths = "institution")
    Optional<EntranceExam> findById(Long id);

    @EntityGraph(attributePaths = "institution")
    List<EntranceExam> findByInstitution(Institution institution);

    @EntityGraph(attributePaths = "institution")
    List<EntranceExam> findByInstitutionId(Long institutionId);

    @EntityGraph(attributePaths = "institution")
    List<EntranceExam> findByYear(short year);
}
