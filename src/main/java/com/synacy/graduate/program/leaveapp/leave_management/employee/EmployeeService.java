package com.synacy.graduate.program.leaveapp.leave_management.employee;

import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }


    public Employee createEmployee(CreateEmployeeRequest createEmployeeRequest) {
        Employee employee = new Employee();
        employee.setFirstName(createEmployeeRequest.getFirstName());
        employee.setLastName(createEmployeeRequest.getLastName());
        employee.setRole(createEmployeeRequest.getRole());
        employee.setTotalLeaves(createEmployeeRequest.getTotalLeaves());
        employee.setAvailableLeaves(createEmployeeRequest.getTotalLeaves());

        if(createEmployeeRequest.getManagerId() == null) {
            employee.setManager(null);
        } else {
            employee.setManager(employeeRepository.findById(createEmployeeRequest.getManagerId()).get());
        }

        employee.setIsDeleted(false);

        return employeeRepository.save(employee);
    }

}
