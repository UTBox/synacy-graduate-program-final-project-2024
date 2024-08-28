package com.synacy.graduate.program.leaveapp.leave_management.employee;

import lombok.Getter;

@Getter
public class EmployeeResponse {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final EmployeeRole role;
    private final Integer totalLeaves;
    private final Integer availableLeaves;
    private final ManagerResponse manager;

    EmployeeResponse(Employee employee) {
        this.id = employee.getId();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.role = employee.getRole();
        this.totalLeaves = employee.getTotalLeaves();
        this.availableLeaves = employee.getAvailableLeaves();

        if(employee.getManager() == null) {
            this.manager = null;
        } else {
            this.manager = new ManagerResponse(employee.getManager());
        }
    }
}
