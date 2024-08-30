package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.synacy.graduate.program.leaveapp.leave_management.employee.Employee;
import com.synacy.graduate.program.leaveapp.leave_management.employee.ManagerResponse;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveApplicationResponse {
    private final Long id;
    private final Employee employee;
    private final ManagerResponse manager;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Integer days;
    private final String reason;
    private final String status;

    public LeaveApplicationResponse(LeaveApplication leaveApplication) {
        this.id = leaveApplication.getId();
        this.employee = leaveApplication.getEmployee();
        this.manager = new ManagerResponse(leaveApplication.getManager());
        this.startDate = leaveApplication.getStartDate();
        this.endDate = leaveApplication.getEndDate();
        this.days = leaveApplication.getWorkDays();
        this.reason = leaveApplication.getReason();
        this.status = leaveApplication.getStatus().getStatusName();
    }
}
