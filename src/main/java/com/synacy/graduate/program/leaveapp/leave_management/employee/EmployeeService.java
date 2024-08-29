package com.synacy.graduate.program.leaveapp.leave_management.employee;

import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.InvalidOperationException;
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
        return employeeRepository.findAll(pageable);
    }

    public Optional<Employee> getEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId);
    }

    public Employee createEmployee(CreateEmployeeRequest createEmployeeRequest) {

        Employee employee = new Employee();

        if(createEmployeeRequest.getRole() == EmployeeRole.HR_ADMIN){
            handleCreateHrAdmin();
        } else if (createEmployeeRequest.getRole() == EmployeeRole.MANAGER){
            handleCreateManager(employee, createEmployeeRequest);
        } else if (createEmployeeRequest.getRole() == EmployeeRole.EMPLOYEE){
            handleCreateEmployee(employee, createEmployeeRequest);
        }

        employee.setFirstName(createEmployeeRequest.getFirstName().strip());
        employee.setLastName(createEmployeeRequest.getLastName().strip());
        employee.setRole(createEmployeeRequest.getRole());
        employee.setTotalLeaves(createEmployeeRequest.getTotalLeaves());
        employee.setAvailableLeaves(createEmployeeRequest.getTotalLeaves());

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

    private void handleCreateHrAdmin() {
        throw new InvalidOperationException("HR_ADMIN_CREATION", "Cannot create an HR Admin employee");
    }

    private void handleCreateManager(Employee employee, CreateEmployeeRequest createEmployeeRequest) {
        Employee manager;

        if(createEmployeeRequest.getManagerId() == null) {
            manager = employeeRepository.findByIdAndIsDeletedIsFalse(1L).get();
        } else {
            manager = employeeRepository.findByIdAndIsDeletedIsFalse(createEmployeeRequest.getManagerId())
                    .orElseThrow(ResourceNotFoundException::new);

        }

        if(manager.getRole() == EmployeeRole.EMPLOYEE ) {
            throw new NotManagerException();
        }

        employee.setManager(manager);
    }

    private void handleCreateEmployee(Employee employee, CreateEmployeeRequest createEmployeeRequest) {
        if(createEmployeeRequest.getManagerId() == null) {
            throw new NoManagerException();
        }

        Employee manager = employeeRepository.findByIdAndIsDeletedIsFalse(createEmployeeRequest.getManagerId())
                .orElseThrow(ResourceNotFoundException::new);

        if(manager.getRole() == EmployeeRole.EMPLOYEE || manager.getRole() == EmployeeRole.HR_ADMIN) {
            throw new NotManagerException();
        }

        employee.setManager(manager);
    }

}
