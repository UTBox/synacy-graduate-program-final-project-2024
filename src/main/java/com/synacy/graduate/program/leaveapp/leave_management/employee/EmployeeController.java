package com.synacy.graduate.program.leaveapp.leave_management.employee;

import com.synacy.graduate.program.leaveapp.leave_management.web.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
