package com.synacy.graduate.program.leaveapp.leave_management.leave;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }
}
