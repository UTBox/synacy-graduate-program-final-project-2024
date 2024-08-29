package com.synacy.graduate.program.leaveapp.leave_management.employee;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateEmployeeRequest {

    @NotNull(message = "First name is null.")
    private String firstName;

    @NotNull(message = "Last name is null.")
    private String lastName;

    @NotNull(message = "Role is null.")
    private EmployeeRole role;

    @Min(value = 0, message = "Total Leaves should be at least 0.")
    private Integer totalLeaves;

    private Long managerId;
}
