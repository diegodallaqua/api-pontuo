package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.Address;
import com.pontuo.api_pontuo.entity.City;
import com.pontuo.api_pontuo.repository.CityRepository;
import com.pontuo.api_pontuo.repository.AddressRepository;
import com.pontuo.api_pontuo.repository.LikePattern;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Regras de endereço. Como todo {@code @Service}, é um bean de escopo
 * singleton: uma única instância, criada pelo contêiner e injetada no
 * controller pelo construtor (<b>Singleton</b> gerenciado pelo Spring). As
 * dependências também chegam por construtor, o que permite substituí-las por
 * mocks nos testes.
 */
@Service
public class AddressService {

    private final AddressRepository repository;
    private final CityRepository cityRepository;

    public AddressService(AddressRepository repository,
                          CityRepository cityRepository) {
        this.repository = repository;
        this.cityRepository = cityRepository;
    }

    public Page<Address> findAll(String search, Pageable pageable) {
        return repository.search(null, LikePattern.contains(search), pageable);
    }

    public Page<Address> findByCityId(Long cityId, String search, Pageable pageable) {
        if (!cityRepository.existsById(cityId)) {
            throw new EntityNotFoundException(
                    "City não encontrada: id=" + cityId);
        }
        return repository.search(cityId, LikePattern.contains(search), pageable);
    }

    public Optional<Address> findById(Long id) {
        return repository.findById(id);
    }

    public Address create(Address address, Long cityId) {
        address.setCity(findCity(cityId));
        return repository.save(address);
    }

    public Address update(Long id, Address updated, Long cityId) {
        Address existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Address não encontrado: id=" + id));
        existing.setStreet(updated.getStreet());
        existing.setNeighborhood(updated.getNeighborhood());
        existing.setNumber(updated.getNumber());
        existing.setComplement(updated.getComplement());
        existing.setCity(findCity(cityId));
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Address não encontrado: id=" + id);
        }
        repository.deleteById(id);
    }

    private City findCity(Long cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "City não encontrada: id=" + cityId));
    }
}
