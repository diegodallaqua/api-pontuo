package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {
}