package com.synacy.graduate.program.leaveapp.leave_management.employee;

import lombok.Getter;

@Getter
public class EmployeeListResponse {
    private Long id;
    private String name;
    private EmployeeRole role;

    EmployeeListResponse(Employee employee){
        this.id = employee.getId();
        this.name = employee.getName();
        this.role = employee.getRole();
    }
}
