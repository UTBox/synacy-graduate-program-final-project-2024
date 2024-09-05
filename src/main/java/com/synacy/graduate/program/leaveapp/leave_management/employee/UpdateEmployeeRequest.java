package com.synacy.graduate.program.leaveapp.leave_management.employee;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateEmployeeRequest {
    @NotNull(message = "Total leaves is required")
    @Min(value = 0, message = "Total leaves must not be less than zero.")
    private Integer totalLeaves;
}
