package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveApplicationResponse {
    private final Long employeeId;
    private final Long managerId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Integer days;
    private final String reason;
    private final String status;

    public LeaveApplicationResponse(LeaveApplication leaveApplication) {
        this.employeeId = leaveApplication.getEmployeeId();
        this.managerId = leaveApplication.getManagerId();
        this.startDate = leaveApplication.getStartDate();
        this.endDate = leaveApplication.getEndDate();
        this.days = leaveApplication.getWorkDays();
        this.reason = leaveApplication.getReason();
        this.status = leaveApplication.getStatus().name();
    }
}
