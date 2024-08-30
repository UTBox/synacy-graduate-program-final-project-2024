package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class LeaveApplicationService {

    private final LeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    public LeaveApplicationService(LeaveApplicationRepository leaveApplicationRepository) {
        this.leaveApplicationRepository = leaveApplicationRepository;
    }

    void createLeaveApplication(CreateLeaveApplicationRequest createLeaveApplicationRequest) {

        Integer leaveWorkDays = calculateLeaveWorkDays(
                createLeaveApplicationRequest.getStartDate(),
                createLeaveApplicationRequest.getEndDate()
        );

        LeaveApplication leaveApplication = new LeaveApplication();
        leaveApplication.setEmployeeId(createLeaveApplicationRequest.getEmployeeId());
        leaveApplication.setManagerId(createLeaveApplicationRequest.getManagerId());
        leaveApplication.setStartDate(createLeaveApplicationRequest.getStartDate());
        leaveApplication.setEndDate(createLeaveApplicationRequest.getEndDate());
        leaveApplication.setWorkDays(leaveWorkDays);

        leaveApplicationRepository.save(leaveApplication);
    }

    private Integer calculateLeaveWorkDays(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidLeaveDateException("Start date or end date cannot be null.");
        } else if (startDate.isAfter(endDate)) {
            throw new InvalidLeaveDateException("Start date cannot be after end date.");
        }

        Integer leaveWorkDays = 0;
        LocalDate currentDate = startDate;

        while(!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                leaveWorkDays++;
            }
            currentDate = currentDate.plusDays(1);
        }

        return leaveWorkDays;
    }
}
