package com.helpdesk.service;

import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.model.SlaPolicy;
import com.helpdesk.repository.SlaPolicyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SlaPolicyService {

    private final SlaPolicyRepository slaPolicyRepository;

    public SlaPolicyService(SlaPolicyRepository slaPolicyRepository) {
        this.slaPolicyRepository = slaPolicyRepository;
    }

    public List<SlaPolicy> findAll() {
        return slaPolicyRepository.findAll();
    }

    public SlaPolicy findByName(String name) {
        return slaPolicyRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Sla policy not found with name: " + name));
    }

    public SlaPolicy findById(UUID id) {
        return slaPolicyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sla policy not found"));
    }

    public SlaPolicy save(SlaPolicy slaPolicy) {
        return slaPolicyRepository.save(slaPolicy);
    }

    public SlaPolicy update(UUID id, SlaPolicy slaPolicy) {
        SlaPolicy existing = findById(id);
        slaPolicy.setId(existing.getId());
        slaPolicy.setCreatedAt(existing.getCreatedAt());
        return slaPolicyRepository.save(slaPolicy);
    }

    public void deleteById(UUID id) {
        slaPolicyRepository.deleteById(id);
    }
}
