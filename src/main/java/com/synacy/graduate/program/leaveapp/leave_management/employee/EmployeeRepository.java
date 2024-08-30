package com.synacy.graduate.program.leaveapp.leave_management.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Page<Employee> findAllByIsDeletedIsFalse(Pageable pageable);
    Optional<Employee> findByIdAndIsDeletedIsFalse(Long id);
}
