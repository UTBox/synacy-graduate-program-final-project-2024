package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateLeaveApplicationRequest {
    @NotNull(message = "Leave status is required")
    private LeaveApplicationStatus leaveApplicationStatus;
}
