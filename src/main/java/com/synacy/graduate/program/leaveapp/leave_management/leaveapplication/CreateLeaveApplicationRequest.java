package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CreateLeaveApplicationRequest {
    @NotNull(message = "Employee id is null")
    private Long employeeId;

    @NotNull(message = "Start date is null")
    private LocalDate startDate;

    @NotNull(message = "End date is null")
    private LocalDate endDate;

    @NotNull(message = "Reason is null")
    private String reason;
}
