package com.synacy.graduate.program.leaveapp.leave_management.employee;

import com.synacy.graduate.program.leaveapp.leave_management.web.PageResponse;
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
    public PageResponse<EmployeeResponse> getEmployees(
            @RequestParam(name = "max", defaultValue = "2")
            @Min(value = 1, message = "Max must be greater than 1") Integer max,
            @RequestParam(name = "page", defaultValue = "1")
            @Min(value = 1, message = "Page must be greater than 1") Integer page) {

        Page<Employee> employees = employeeService.getEmployees(max, page);
        long employeeCount = employees.getTotalElements();
        List<EmployeeResponse> employeeResponseList = employees
                .stream()
                .map(EmployeeResponse::new)
                .collect(Collectors.toList());

        return new PageResponse<>(employeeCount, page, employeeResponseList);
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
            throw new InvalidRequestException("Provided manager does not have a Manager role.");
        } catch (NoManagerException e) {
            throw new InvalidRequestException("No manager provided.");
        }
    }

    @PutMapping("api/v1/employee/{id}")
    public UpdateEmployeeResponse updateEmployee(@PathVariable(name = "id") Long id, @RequestBody UpdateEmployeeRequest updateEmployeeRequest) {
        Employee existingEmployee = employeeService.getEmployeeById(id).orElseThrow(ResourceNotFoundException::new);

        try {
            Employee employee = employeeService.updateEmployee(existingEmployee, updateEmployeeRequest);
            return new UpdateEmployeeResponse(employee);
        } catch (RuntimeException e) {
            throw new RuntimeException();
        }
    }
}
