package com.synacy.graduate.program.leaveapp.leave_management.employee;

import lombok.Getter;

@Getter
public class EmployeeResponse {
    private final Long id;
    private final String employeeName;
    private final EmployeeRole role;
    private final Employee manager;
    private final int totalLeaves;
    private final int availableLeaves;

    public EmployeeResponse(Employee employee) {
        this.id = employee.getId();
        this.employeeName = employee.getFirstName() + " " + employee.getLastName();
        this.role = employee.getRole();
        this.manager = employee.getManager();
        this.totalLeaves = employee.getTotalLeaves();
        this.availableLeaves = employee.getAvailableLeaves();
    }
}
