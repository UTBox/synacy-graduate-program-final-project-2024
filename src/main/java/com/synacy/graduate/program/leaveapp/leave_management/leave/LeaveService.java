package com.synacy.graduate.program.leaveapp.leave_management.leave;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class LeaveService {

    void createLeaveApplication(CreateLeaveRequest createLeaveRequest) {

        LeaveApplication leaveApplication = new LeaveApplication();

    }

    private Integer calculateLeaveWorkDays(LocalDate startDate, LocalDate endDate) {
        Integer workDays = 0;
        LocalDate currentDate = startDate;

        while(!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                workDays++;
            }
            currentDate = currentDate.plusDays(1);
        }

        return workDays;
    }
}
