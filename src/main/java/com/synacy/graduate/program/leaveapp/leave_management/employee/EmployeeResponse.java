package com.synacy.graduate.program.leaveapp.leave_management.employee;

public class EmployeeResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private EmployeeRole role;
    private Integer totalLeaves;
    private Integer availableLeaves;
    private ManagerResponse manager;
}
