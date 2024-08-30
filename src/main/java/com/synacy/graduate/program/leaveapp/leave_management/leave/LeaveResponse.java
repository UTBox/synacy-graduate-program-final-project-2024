package com.synacy.graduate.program.leaveapp.leave_management.leave;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveResponse {
    private final Long employeeId;
    private final Long managerId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Integer days;
    private final String reason;
    private final String status;

    public LeaveResponse(LeaveApplication leaveApplication) {
        this.employeeId = leaveApplication.getEmployeeId();
        this.startDate = leaveApplication.getStartDate();
        this.endDate = leaveApplication.getEndDate();
        this.days = leaveApplication.getWorkDays();
        this.reason = leaveApplication.getReason();
        this.status = leaveApplication.getStatus().name();

        if (leaveApplication.getManagerId() == null) {
            this.managerId = null;
        } else {
            this.managerId = leaveApplication.getManagerId();
        }
    }
}
