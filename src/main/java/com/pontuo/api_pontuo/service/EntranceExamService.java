package com.pontuo.api_pontuo.service;

import com.pontuo.api_pontuo.entity.EntranceExam;
import com.pontuo.api_pontuo.entity.Institution;
import com.pontuo.api_pontuo.repository.InstitutionRepository;
import com.pontuo.api_pontuo.repository.EntranceExamRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EntranceExamService {

    private final EntranceExamRepository repository;
    private final InstitutionRepository institutionRepository;


    public EntranceExamService(EntranceExamRepository repository, InstitutionRepository institutionRepository) {
        this.repository = repository;
        this.institutionRepository = institutionRepository;
    }

    public List<EntranceExam> findAll() {
        return repository.findAll();
    }

    public List<EntranceExam> findByYear(short year) {
        return repository.findByYear(year);
    }

    public List<EntranceExam> findByInstitution(Institution institution) {
        return repository.findByInstitution(institution);
    }

    public List<EntranceExam> findByInstitutionId(Long institutionId) {
        return repository.findByInstitutionId(institutionId);
    }

    public Optional<EntranceExam> findById(Long id) {
        return repository.findById(id);
    }

    public EntranceExam create(EntranceExam entranceExam, Long institutionId) {
        entranceExam.setInstitution(findInstitution(institutionId));
        return repository.save(entranceExam);
    }

    public EntranceExam update(Long id, EntranceExam updated, Long institutionId) {
        EntranceExam existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "EntranceExam não encontrado: id=" + id));
        existing.setName(updated.getName());
        existing.setYear(updated.getYear());
        existing.setStage(updated.getStage());
        existing.setInstitution(findInstitution(institutionId));
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("EntranceExam não encontrado: id=" + id);
        }
        repository.deleteById(id);
    }

    private Institution findInstitution(Long institutionId) {
        return institutionRepository.findById(institutionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Institution não encontrada: id=" + institutionId));
    }
}
