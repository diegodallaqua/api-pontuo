package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.Address;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <b>Repository</b> de {@link Address}. A implementação é gerada pelo Spring
 * Data a partir da assinatura dos métodos; {@code @EntityGraph} carrega a
 * cidade junto do endereço, evitando consultas N+1 na listagem.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Override
    @EntityGraph(attributePaths = "city")
    List<Address> findAll();

    @Override
    @EntityGraph(attributePaths = "city")
    Optional<Address> findById(Long id);

    @EntityGraph(attributePaths = "city")
    List<Address> findByCityId(Long cityId);
}
