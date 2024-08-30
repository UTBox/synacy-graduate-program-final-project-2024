package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaveApplicationController {

    private final LeaveApplicationService leaveApplicationService;

    public LeaveApplicationController(LeaveApplicationService leaveApplicationService) {
        this.leaveApplicationService = leaveApplicationService;
    }

//    @ResponseStatus(HttpStatus.CREATED)
//    @PostMapping("api/v1/leave")
//    public LeaveResponse createLeaveApplication(@RequestBody @Valid CreateLeaveRequest) {
//
//    }
}
