package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

import lombok.Getter;

public enum LeaveApplicationStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    CANCELLED("Cancelled");

    @Getter
    private String statusName;
    LeaveApplicationStatus(String statusName) {
        this.statusName = statusName;
    }
}
