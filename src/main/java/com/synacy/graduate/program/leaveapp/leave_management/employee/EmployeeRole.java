package com.synacy.graduate.program.leaveapp.leave_management.employee;

import lombok.Getter;

public enum EmployeeRole {
    EMPLOYEE("Employee"),
    MANAGER("Manager"),
    ADMIN("Admin");

    @Getter
    private String name;
    EmployeeRole(String name){
        this.name = name;
    }
}
