package com.synacy.graduate.program.leaveapp.leave_management.employee;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    public Optional<Employee> findByIdAndIsDeletedIsFalse(Long id);

    @Query("SELECT e " +
            "FROM employee e " +
            "WHERE CONCAT(e.firstName,' ',e.lastName) ILIKE CONCAT('%',:name,'%') " +
            "AND e.role = 'MANAGER' " +
            "AND e.isDeleted = false " +
            "ORDER BY e.id " +
            "LIMIT 10")
    public List<Employee> findFirst10ManagersByName(String name);

    @Query("SELECT e " +
            "FROM employee e " +
            "WHERE e.role = 'MANAGER' " +
            "AND e.isDeleted = false " +
            "ORDER BY e.id " +
            "LIMIT 10")
    public List<Employee> findFirst10Managers();
}
