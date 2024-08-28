package com.synacy.graduate.program.leaveapp.leave_management.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final List<Employee> employeesList;

    @Autowired
    public EmployeeService(List<Employee> employeesList, EmployeeRepository employeeRepository) {
        this.employeesList = employeesList;
        this.employeeRepository = employeeRepository;
        createInitialEmployees();
    }

    public Page<Employee> getEmployees(int max, int page) {
        Pageable pageable = PageRequest.of(page - 1, max, Sort.by("id"));
        return employeeRepository.findAll(pageable);
    }

    private void createInitialEmployees() {
        employeeRepository.saveAll(employeesList);
    }
}
