package com.synacy.graduate.program.leaveapp.leave_management.web.apierror;

public class InvalidRequestException extends RuntimeException {
    private final String errorMessage;

    public InvalidRequestException(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
