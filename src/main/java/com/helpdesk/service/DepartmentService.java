package com.helpdesk.service;

import com.helpdesk.exception.ResourceNotFoundException;
import com.helpdesk.model.Department;
import com.helpdesk.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    public Department findById(UUID id) {
        return departmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department not found"));
    }

    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    public Department update(UUID id, Department department) {
        Department existing = findById(id);
        department.setId(existing.getId());
        department.setCreatedAt(existing.getCreatedAt());
        return departmentRepository.save(department);
    }

    public void deleteById(UUID id) {
        departmentRepository.deleteById(id);
    }
}
