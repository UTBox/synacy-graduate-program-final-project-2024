package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CreateLeaveApplicationRequest {
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
}
