package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ManagerialLeaveApplicationResponse {
    private final Long id;
    private final String employeeName;
    private final String managerName;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Integer workDays;
    private final String reason;
    private final LeaveApplicationStatus status;

    public ManagerialLeaveApplicationResponse(LeaveApplication leaveApplication) {
        this.id = leaveApplication.getId();
        this.employeeName = leaveApplication.getEmployee().getFirstName() + " " + leaveApplication.getEmployee().getLastName();
        this.managerName = leaveApplication.getManager().getFirstName() + " " + leaveApplication.getManager().getLastName();
        this.startDate = leaveApplication.getStartDate();
        this.endDate = leaveApplication.getEndDate();
        this.workDays = leaveApplication.getWorkDays();
        this.reason = leaveApplication.getReason();
        this.status = leaveApplication.getStatus();
    }
}
