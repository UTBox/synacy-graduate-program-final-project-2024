package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

import com.synacy.graduate.program.leaveapp.leave_management.employee.Employee;
import com.synacy.graduate.program.leaveapp.leave_management.employee.EmployeeService;
import com.synacy.graduate.program.leaveapp.leave_management.web.PageResponse;
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.ResourceNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LeaveApplicationController {

    private final LeaveApplicationService leaveApplicationService;
    private final EmployeeService employeeService;

    public LeaveApplicationController(LeaveApplicationService leaveApplicationService, EmployeeService employeeService) {
        this.leaveApplicationService = leaveApplicationService;
        this.employeeService = employeeService;
    }

    @GetMapping("api/v1/leave")
    public PageResponse<ManagerialLeaveApplicationResponse> getLeaveApplications(
            @RequestParam(name = "max", defaultValue = "2")
            @Min(value = 1, message = "Max must be greater than 1") Integer max,
            @RequestParam(name = "page", defaultValue = "1")
            @Min(value = 1, message = "Page must be greater than 1") Integer page
    ){
        Page<LeaveApplication> leaveApplications = leaveApplicationService.getPendingLeaveApplications(max, page);
        long totalCount = leaveApplications.getTotalElements();
        List<ManagerialLeaveApplicationResponse> leaveApplicationList = leaveApplications
                .getContent()
                .stream()
                .map(ManagerialLeaveApplicationResponse::new)
                .collect(Collectors.toList());
        return new PageResponse<>(totalCount, page, leaveApplicationList);
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
