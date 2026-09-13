package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.Address;
import com.pontuo.api_pontuo.entity.City;
import com.pontuo.api_pontuo.repository.CityRepository;
import com.pontuo.api_pontuo.repository.AddressRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository repository;
    private final CityRepository cityRepository;

    public AddressService(AddressRepository repository,
                          CityRepository cityRepository) {
        this.repository = repository;
        this.cityRepository = cityRepository;
    }

    public List<Address> findAll() {
        return repository.findAll();
    }

    public List<Address> findByCityId(Long cityId) {
        if (!cityRepository.existsById(cityId)) {
            throw new EntityNotFoundException(
                    "City não encontrada: id=" + cityId);
        }
        return repository.findByCityId(cityId);
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
