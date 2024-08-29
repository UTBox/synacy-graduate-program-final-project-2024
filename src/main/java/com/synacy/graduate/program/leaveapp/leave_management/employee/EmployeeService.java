package com.synacy.graduate.program.leaveapp.leave_management.employee;

import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        return employeeRepository.findAllByIsDeletedIsFalse(pageable);
    }

    public Optional<Employee> getEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId);
    }

    public Employee createEmployee(CreateEmployeeRequest createEmployeeRequest) {
        Employee employee = new Employee();
        employee.setFirstName(createEmployeeRequest.getFirstName().strip());
        employee.setLastName(createEmployeeRequest.getLastName().strip());
        employee.setRole(createEmployeeRequest.getRole());
        employee.setTotalLeaves(createEmployeeRequest.getTotalLeaves());
        employee.setAvailableLeaves(createEmployeeRequest.getTotalLeaves());

        if (createEmployeeRequest.getManagerId() == null) {
            handleNullManager(employee, createEmployeeRequest);
        } else {
            Employee manager = employeeRepository.findById(createEmployeeRequest.getManagerId())
                    .orElseThrow(ResourceNotFoundException::new);

            boolean isNotManager = manager.getRole() != EmployeeRole.MANAGER;
            if (isNotManager) {
                throw new NotManagerException();
            }

            employee.setManager(manager);
        }

        employee.setIsDeleted(false);
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Employee selectedEmployee, UpdateEmployeeRequest updateEmployeeRequest) {
        selectedEmployee.setTotalLeaves(updateEmployeeRequest.getTotalLeaveCredits());

        return employeeRepository.save(selectedEmployee);
    }

    private void createInitialEmployees() {
        employeeRepository.saveAll(employeesList);
    }

    private void handleNullManager(Employee employee, CreateEmployeeRequest createEmployeeRequest) {
        if (createEmployeeRequest.getRole() == EmployeeRole.EMPLOYEE) {
            throw new NoManagerException();
        }

        employee.setManager(null);
    }
}
