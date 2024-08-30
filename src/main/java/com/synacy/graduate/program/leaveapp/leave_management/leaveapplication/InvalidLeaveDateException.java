package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

public class InvalidLeaveDateException extends RuntimeException {
    public InvalidLeaveDateException(String message) {
        super(message);
    }
}
