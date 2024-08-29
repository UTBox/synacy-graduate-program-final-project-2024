package com.synacy.graduate.program.leaveapp.leave_management.employee;

import lombok.Getter;

@Getter
public class ManagerResponse {
    private Long id;
    private String firstName;
    private String lastName;

    ManagerResponse(Employee manager){
        this.id = manager.getId();
        this.firstName = manager.getFirstName();
        this.lastName = manager.getLastName();
    }
}
