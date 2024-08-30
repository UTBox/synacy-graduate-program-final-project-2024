package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

import com.synacy.graduate.program.leaveapp.leave_management.employee.Employee;
import com.synacy.graduate.program.leaveapp.leave_management.employee.EmployeeRepository;
import com.synacy.graduate.program.leaveapp.leave_management.employee.EmployeeService;
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class LeaveApplicationService {

    private final LeaveApplicationRepository leaveApplicationRepository;
    private final EmployeeService employeeService;

    @Autowired
    public LeaveApplicationService(LeaveApplicationRepository leaveApplicationRepository, EmployeeService employeeService) {
        this.leaveApplicationRepository = leaveApplicationRepository;
        this.employeeService = employeeService;
    }

    Page<LeaveApplication> getLeavesByManager(int max, int page, Long managerId){
        Pageable pageable = PageRequest.of(page - 1, max, Sort.by("id"));
        Employee manager = employeeService.getEmployeeById(managerId).get();
        return leaveApplicationRepository.findAllByManager(manager, pageable);
    }

    LeaveApplication createLeaveApplication(Employee employee, CreateLeaveApplicationRequest createLeaveApplicationRequest) {
        Integer leaveWorkDays = calculateLeaveWorkDays(
                createLeaveApplicationRequest.getStartDate(),
                createLeaveApplicationRequest.getEndDate()
        );

        LeaveApplication leaveApplication = new LeaveApplication();
        leaveApplication.setEmployee(employee);
        leaveApplication.setManager(employee.getManager());
        leaveApplication.setStartDate(createLeaveApplicationRequest.getStartDate());
        leaveApplication.setEndDate(createLeaveApplicationRequest.getEndDate());
        leaveApplication.setWorkDays(leaveWorkDays);
        leaveApplication.setReason(createLeaveApplicationRequest.getReason());
        leaveApplication.setStatus(LeaveApplicationStatus.PENDING);

        return leaveApplicationRepository.save(leaveApplication);
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
