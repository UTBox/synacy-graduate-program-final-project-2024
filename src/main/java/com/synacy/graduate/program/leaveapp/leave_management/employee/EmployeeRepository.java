package com.synacy.graduate.program.leaveapp.leave_management.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    public Optional<Employee> findByIdAndIsDeletedIsFalse(Long id);
}
