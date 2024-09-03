package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

import com.synacy.graduate.program.leaveapp.leave_management.employee.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
    Page<LeaveApplication> findAllByStatus(LeaveApplicationStatus status, Pageable pageable);
    Page<LeaveApplication> findAllByManager(Employee manager, Pageable pageable);
    Page<LeaveApplication> findAllByEmployee(Employee employee, Pageable pageable);

    @Query(value = "SELECT COUNT(*) " +
            "FROM leave_application l " +
            "WHERE employee_id = :employeeId " +
            "AND NOT (end_date < :startDate OR start_date > :endDate) " +
            "AND status NOT IN ('REJECTED', 'CANCELLED')",
            nativeQuery = true
    )
    Integer countOverlappingLeaveApplications(Long employeeId, LocalDate startDate, LocalDate endDate);
}
