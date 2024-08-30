package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EmployeeLeaveApplicationResponse {
    private final Long id;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Integer workDays;
    private final String reason;
    private final LeaveApplicationStatus status;

    public EmployeeLeaveApplicationResponse(LeaveApplication leaveApplication) {
        this.id = leaveApplication.getId();
        this.startDate = leaveApplication.getStartDate();
        this.endDate = leaveApplication.getEndDate();
        this.workDays = leaveApplication.getWorkDays();
        this.reason = leaveApplication.getReason();
        this.status = leaveApplication.getStatus();
    }
}
