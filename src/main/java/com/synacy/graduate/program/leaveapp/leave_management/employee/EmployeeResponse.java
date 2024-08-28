package com.synacy.graduate.program.leaveapp.leave_management.employee;

import lombok.Getter;

@Getter
public class EmployeeResponse {
    private final Long id;
    private final String fullName;
    private final EmployeeRole role;
    private final int availableLeaves;

    public EmployeeResponse(Employee employee) {
        this.id = employee.getId();
        this.fullName = employee.getFirstName() + " " + employee.getLastName();
        this.role = employee.getRole();
        this.availableLeaves = employee.getAvailableLeaves();
    }
}
