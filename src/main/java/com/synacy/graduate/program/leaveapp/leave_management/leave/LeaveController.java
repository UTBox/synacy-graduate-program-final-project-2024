package com.synacy.graduate.program.leaveapp.leave_management.leave;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

//    @ResponseStatus(HttpStatus.CREATED)
//    @PostMapping("api/v1/leave")
//    public LeaveResponse createLeaveApplication(@RequestBody @Valid CreateLeaveRequest) {
//
//    }
}
