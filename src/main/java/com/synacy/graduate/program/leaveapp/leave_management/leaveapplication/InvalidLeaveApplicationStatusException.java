package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

public class InvalidLeaveApplicationStatusException extends RuntimeException {
    public InvalidLeaveApplicationStatusException(String message) {
        super(message);
    }
}
