package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.InvalidOperationException;
import com.synacy.graduate.program.leaveapp.leave_management.web.PageResponse;
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.InvalidRequestException;
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

    public LeaveApplicationController(LeaveApplicationService leaveApplicationService) {
        this.leaveApplicationService = leaveApplicationService;
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
        int totalPages = leaveApplications.getTotalPages();
        List<ManagerialLeaveApplicationResponse> leaveApplicationList = leaveApplications
                .getContent()
                .stream()
                .map(ManagerialLeaveApplicationResponse::new)
                .collect(Collectors.toList());
        return new PageResponse<>(totalCount, totalPages, page, leaveApplicationList);
    }

    @GetMapping("/api/v1/leave/manager/{id}")
    public PageResponse<ManagerialLeaveApplicationResponse> getLeavesByManager(
            @RequestParam(name = "max", defaultValue = "2")
            @Min(value = 1, message = "Max must be greater than 1") Integer max,
            @RequestParam(name = "page", defaultValue = "1")
            @Min(value = 1, message = "Page must be greater than 1") Integer page,
            @PathVariable(name = "id") Long managerId
    ) {
        try {
            Page<LeaveApplication> leaveApplications = leaveApplicationService.getLeavesByManager(max, page, managerId);
            long count = leaveApplications.getTotalElements();
            int totalPages = leaveApplications.getTotalPages();
            List<ManagerialLeaveApplicationResponse> leaveApplicationResponseList = leaveApplications
                    .getContent()
                    .stream()
                    .map(ManagerialLeaveApplicationResponse::new)
                    .collect(Collectors.toList());

            return new PageResponse<>(count, totalPages, page, leaveApplicationResponseList);
        } catch (NotAManagerException e) {
            throw new InvalidOperationException("NOT_A_MANAGER", "The role of the employee associated with the ID is not a MANAGER");
        } catch (ResourceNotFoundException e) {
            throw new InvalidRequestException("No employee is associated with the ID");
        }
    }

    @GetMapping("/api/v1/leave/employee/{id}")
    public PageResponse<EmployeeLeaveApplicationResponse> getLeaveByEmployee(
            @RequestParam(name = "max", defaultValue = "2")
            @Min(value = 1, message = "Max must be greater than 1") Integer max,
            @RequestParam(name = "page", defaultValue = "1")
            @Min(value = 1, message = "Page must be greater than 1") Integer page,
            @PathVariable(name = "id") Long employeeId
    ) {
        try {
            Page<LeaveApplication> leaveApplications = leaveApplicationService.getLeavesByEmployee(max, page, employeeId);
            long count = leaveApplications.getTotalElements();
            int totalPages = leaveApplications.getTotalPages();
            List<EmployeeLeaveApplicationResponse> leaveApplicationResponseList = leaveApplications
                    .getContent()
                    .stream()
                    .map(EmployeeLeaveApplicationResponse::new)
                    .collect(Collectors.toList());

            return new PageResponse<>(count, totalPages, page, leaveApplicationResponseList);
        } catch (ResourceNotFoundException e) {
            throw new InvalidRequestException("No employee is associated with the ID");
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("api/v1/leave")
    public EmployeeLeaveApplicationResponse createLeaveApplication(@RequestBody @Valid CreateLeaveApplicationRequest createLeaveApplicationRequest) {
        try {
            LeaveApplication leaveApplication = leaveApplicationService.createLeaveApplication(createLeaveApplicationRequest);

            return new EmployeeLeaveApplicationResponse(leaveApplication);
        } catch (InvalidLeaveDateException e) {
            throw new InvalidOperationException("INVALID_LEAVE_DATES", e.getMessage());
        } catch (InvalidLeaveApplicationException e) {
            throw new InvalidOperationException("INSUFFICIENT_LEAVE_CREDITS", e.getMessage());
        } catch (ResourceNotFoundException e) {
            throw new InvalidRequestException("Employee does not exist");
        }
    }

    @PutMapping("/api/v1/leave/{id}")
    public EmployeeLeaveApplicationResponse updateLeaveApplication(
            @PathVariable(name = "id") Long id,
            @Valid @RequestBody UpdateLeaveApplicationRequest updateLeaveApplicationRequest) {
        LeaveApplication existingLeaveApplication =
                leaveApplicationService.getLeaveApplicationById(id).orElseThrow(ResourceNotFoundException::new);
        try {
            LeaveApplication leaveApplication = leaveApplicationService.updateLeaveApplication(existingLeaveApplication, updateLeaveApplicationRequest);
            return new EmployeeLeaveApplicationResponse(leaveApplication);

        } catch (StatusNotPendingException e) {
            throw new InvalidOperationException("LEAVE_STATUS_NOT_PENDING", e.getMessage());

        } catch (InvalidLeaveApplicationException e) {
            throw new InvalidOperationException("CANCELLATION_NOT_ALLOWED", e.getMessage());
        }
    }

    @DeleteMapping("api/v1/leave/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelLeaveApplication(@PathVariable(name = "id") Long id) {
        LeaveApplication leave = leaveApplicationService.getLeaveApplicationById(id).orElseThrow(ResourceNotFoundException::new);
        try {
            leaveApplicationService.cancelLeaveApplication(leave);
        } catch (StatusNotPendingException e) {
            throw new InvalidOperationException("LEAVE_STATUS_NOT_PENDING", e.getMessage());
        }
    }

}
