package com.synacy.graduate.program.leaveapp.leave_management.employee;

public class EmployeeModificationNotAllowedException extends RuntimeException{
    public EmployeeModificationNotAllowedException(String message) {
        super(message);
    }
}
