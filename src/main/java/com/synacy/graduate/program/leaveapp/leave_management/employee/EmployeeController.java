package com.synacy.graduate.program.leaveapp.leave_management.employee;

import com.synacy.graduate.program.leaveapp.leave_management.web.PageResponse;
import jakarta.validation.Valid;
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

    @GetMapping("api/v1/employees")
    public PageResponse<EmployeeResponse> getEmployees(
            @RequestParam(name = "max", defaultValue = "2") Integer max,
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

        Page<Employee> employees = employeeService.getEmployees(max, page);
        long employeeCount = employees.getTotalElements();
        List<EmployeeResponse> employeeResponseList = employees
                .stream()
                .map(EmployeeResponse::new)
                .collect(Collectors.toList());

        return new PageResponse<>(employeeCount, page, employeeResponseList);
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/employee")
    public EmployeeResponse createEmployee(@RequestBody @Valid CreateEmployeeRequest createEmployeeRequest) {
        Employee employee = employeeService.createEmployee(createEmployeeRequest);
        return new EmployeeResponse(employee);
    }

    @PutMapping("api/v1/employee/{id}")
    public UpdateEmployeeResponse updateEmployee(@PathVariable Long id, @RequestBody UpdateEmployeeRequest updateEmployeeRequest) {
        /*
            TODO: Update exception being thrown once custom exceptions have been created.
             8/28/24 21:25
         */
        try {
            Employee employee = getEmployee(id).orElseThrow(RuntimeException::new);

            return new UpdateEmployeeResponse(employeeService.updateEmployee(employee, updateEmployeeRequest));
        } catch (RuntimeException e) {
            throw new RuntimeException();
        }
    }

}
