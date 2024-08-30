package com.synacy.graduate.program.leaveapp.leave_management.employee;

public class InvalidUpdatedTotalLeavesException extends RuntimeException {
    public InvalidUpdatedTotalLeavesException() {
        super("The updated total leaves cannot be less than the available leave credits.");
    }
}
