package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.Address;
import com.pontuo.api_pontuo.entity.Institution;
import com.pontuo.api_pontuo.repository.InstitutionRepository;
import com.pontuo.api_pontuo.repository.AddressRepository;
import com.pontuo.api_pontuo.repository.LikePattern;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InstitutionService {

    private final InstitutionRepository repository;
    private final AddressRepository addressRepository;

    public InstitutionService(InstitutionRepository repository, AddressRepository addressRepository) {
        this.repository = repository;
        this.addressRepository = addressRepository;
    }

    public Page<Institution> findAll(String search, Pageable pageable) {
        return repository.search(null, LikePattern.contains(search), pageable);
    }

    public Page<Institution> findByAcronym(String acronym, String search, Pageable pageable) {
        return repository.search(acronym, LikePattern.contains(search), pageable);
    }

    public Optional<Institution> findById(Long id) {
        return repository.findById(id);
    }

    public Institution create(Institution institution, Long addressId) {
        institution.setAddress(findAddress(addressId));
        return repository.save(institution);
    }

    public Institution update(Long id, Institution updated, Long addressId) {
        Institution existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Institution não encontrada: id=" + id));
        existing.setName(updated.getName());
        existing.setAcronym(updated.getAcronym());
        existing.setAddress(findAddress(addressId));
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Institution não encontrada: id=" + id);
        }
        repository.deleteById(id);
    }

    private Address findAddress(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Address não encontrada: id=" + addressId));
    }
}
