package com.pontuo.api_pontuo.repository;

import com.pontuo.api_pontuo.entity.Address;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * <b>Repository</b> de {@link Address}. A implementação é gerada pelo Spring
 * Data a partir da assinatura dos métodos; {@code @EntityGraph} carrega a
 * cidade junto do endereço, evitando consultas N+1 na listagem.
 */
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Override
    @EntityGraph(attributePaths = "city")
    Optional<Address> findById(Long id);

    /**
     * Listagem: cada filtro nulo se anula, e {@code search} é o padrão de
     * {@link LikePattern#contains}.
     */
    @EntityGraph(attributePaths = "city")
    @Query("""
            SELECT a FROM Address a
            WHERE (:cityId IS NULL OR a.city.id = :cityId)
              AND (:search IS NULL
                   OR LOWER(a.street) LIKE :search ESCAPE '!'
                   OR LOWER(a.neighborhood) LIKE :search ESCAPE '!'
                   OR LOWER(a.complement) LIKE :search ESCAPE '!')
            """)
    Page<Address> search(@Param("cityId") Long cityId, @Param("search") String search, Pageable pageable);
}
