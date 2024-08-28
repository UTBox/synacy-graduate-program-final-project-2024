package com.synacy.graduate.program.leaveapp.leave_management.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Page<Employee> getEmployees(int max, int page) {
        Pageable pageable = PageRequest.of(page - 1, max, Sort.by("id"));
        return employeeRepository.findAll(pageable);
    }

    public UpdateEmployeeResponse updateEmployee(Optional<Employee> selectedEmployee, Optional<Integer> updatedTotalLeaveCredits) {
        /* TODO: Update exceptions being thrown once custom exceptions have been created.
            08/28/24 16:41
         */
        Employee employee = selectedEmployee.orElseThrow(RuntimeException::new);
        Integer totalLeaveCredits = updatedTotalLeaveCredits.orElseThrow(RuntimeException::new);
        employee.setTotalLeaves(totalLeaveCredits);

        return new UpdateEmployeeResponse(employee);
    }
}
