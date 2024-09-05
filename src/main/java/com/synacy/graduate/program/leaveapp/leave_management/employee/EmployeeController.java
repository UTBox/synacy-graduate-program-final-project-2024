package com.synacy.graduate.program.leaveapp.leave_management.employee;

import com.synacy.graduate.program.leaveapp.leave_management.web.PageResponse;
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.InvalidOperationException;
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.InvalidRequestException;
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.ResourceNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/api/v1/employee")
    public PageResponse<EmployeeResponse> getPaginatedEmployees(
            @RequestParam(name = "max", defaultValue = "2")
            @Min(value = 1, message = "Max must be greater than 0") Integer max,
            @RequestParam(name = "page", defaultValue = "1")
            @Min(value = 1, message = "Page must be greater than 0") Integer page) {

        Page<Employee> employees = employeeService.getPaginatedEmployees(max, page);
        long employeeCount = employees.getTotalElements();
        int totalPages = employees.getTotalPages();
        List<EmployeeResponse> employeeResponseList = employees
                .getContent()
                .stream()
                .map(EmployeeResponse::new)
                .collect(Collectors.toList());

        return new PageResponse<>(employeeCount, totalPages, page, employeeResponseList);
    }

    @GetMapping("/api/v1/list/employee")
    public List<EmployeeListResponse> getListEmployees(@RequestParam(name = "name", required = false) String name) {
        List<Employee> employeesList;

        if (name != null) {
            employeesList = employeeService.getListEmployeesByName(name);
        } else {
            employeesList = employeeService.getListEmployees();
        }

        return employeesList.stream()
                .map(EmployeeListResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/v1/list/manager")
    public List<ManagerResponse> getListManagers(@RequestParam(name = "name", required = false) String name) {

        List<Employee> managersList;

        if (name != null) {
            managersList = employeeService.getListManagersByName(name);
        } else {
            managersList = employeeService.getListManagers();
        }

        return managersList.stream()
                .map(ManagerResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/v1/employee/{id}")
    public EmployeeResponse getEmployee(@PathVariable(name = "id") Long id) {
        Employee employee = employeeService.getEmployeeById(id).orElseThrow(ResourceNotFoundException::new);

        return new EmployeeResponse(employee);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/employee")
    public EmployeeResponse createEmployee(@RequestBody @Valid CreateEmployeeRequest createEmployeeRequest) {
        try {
            Employee employee = employeeService.createEmployee(createEmployeeRequest);
            return new EmployeeResponse(employee);
        } catch (ResourceNotFoundException e) {
            throw new InvalidRequestException("Provided manager does not exist.");
        } catch (NotManagerException e) {
            throw new InvalidRequestException("Provided manager cannot manage the employee.");
        } catch (NoManagerException e) {
            throw new InvalidRequestException("No manager provided.");
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("api/v1/employee/{id}")
    public EmployeeResponse updateEmployee(@PathVariable(name = "id") Long id, @Valid @RequestBody UpdateEmployeeRequest updateEmployeeRequest) {
        try {
            Employee updatedEmployee = employeeService.updateEmployee(id, updateEmployeeRequest);
            return new EmployeeResponse(updatedEmployee);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException();
        } catch (EmployeeModificationNotAllowedException e) {
            throw new InvalidOperationException("MODIFICATION_NOT_ALLOWED", e.getMessage());
        } catch (LeaveCountModificationException e) {
            throw new InvalidOperationException("INVALID_LEAVE_MODIFICATION", e.getMessage());
        }
    }
}
