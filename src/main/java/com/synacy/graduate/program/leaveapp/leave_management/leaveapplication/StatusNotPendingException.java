package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

public class StatusNotPendingException extends RuntimeException {
    public StatusNotPendingException(String message) {
        super(message);
    }
}
