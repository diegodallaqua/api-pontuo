package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.City;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    @Override
    @EntityGraph(attributePaths = "state")
    List<City> findAll();

    @Override
    @EntityGraph(attributePaths = "state")
    Optional<City> findById(Long id);

    @EntityGraph(attributePaths = "state")
    List<City> findByStateId(Long stateId);
}
