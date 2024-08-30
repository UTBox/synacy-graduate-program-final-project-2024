package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

import com.synacy.graduate.program.leaveapp.leave_management.employee.Employee;
import com.synacy.graduate.program.leaveapp.leave_management.employee.EmployeeService;
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaveApplicationController {

    private final LeaveApplicationService leaveApplicationService;
    private final EmployeeService employeeService;

    public LeaveApplicationController(LeaveApplicationService leaveApplicationService, EmployeeService employeeService) {
        this.leaveApplicationService = leaveApplicationService;
        this.employeeService = employeeService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("api/v1/leave")
    public EmployeeLeaveApplicationResponse createLeaveApplication(@RequestBody @Valid CreateLeaveApplicationRequest createLeaveApplicationRequest) {
        Employee employee = employeeService.getEmployeeById(
                createLeaveApplicationRequest.getEmployeeId()
        ).orElseThrow(ResourceNotFoundException::new);

        LeaveApplication leaveApplication = leaveApplicationService.createLeaveApplication(employee, createLeaveApplicationRequest);
        return new EmployeeLeaveApplicationResponse(leaveApplication);
    }
}
