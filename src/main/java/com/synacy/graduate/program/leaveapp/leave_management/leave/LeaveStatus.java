package com.synacy.graduate.program.leaveapp.leave_management.leave;

import lombok.Getter;

public enum LeaveStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    CANCELLED("Cancelled");

    @Getter
    private String statusName;
    LeaveStatus(String statusName) {
        this.statusName = statusName;
    }
}
