package com.synacy.graduate.program.leaveapp.leave_management.web.apierror;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String errorMessage;

    public ResourceNotFoundException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}