package com.synacy.graduate.program.leaveapp.leave_management.leave;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CreateLeaveRequest {
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
}
