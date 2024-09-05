//package com.synacy.graduate.program.leaveapp.leave_management.employee;
//
//import lombok.Getter;
//
//@Getter
//public class UpdateEmployeeResponse {
//    private final Long id;
//    private final String firstName;
//    private final String lastName;
//    private final EmployeeRole role;
//    private final Long managerId;
//    private final Integer totalLeaves;
//    private final Integer availableLeaves;
//    private final Boolean isDeleted;
//
//    public UpdateEmployeeResponse(Employee employee) {
//        this.id = employee.getId();
//        this.firstName = employee.getFirstName();
//        this.lastName = employee.getLastName();
//        this.role = employee.getRole();
//        this.managerId = employee.getManager().getId();
//        this.totalLeaves = employee.getTotalLeaves();
//        this.availableLeaves = employee.getAvailableLeaves();
//        this.isDeleted = employee.getIsDeleted();
//    }
//}
