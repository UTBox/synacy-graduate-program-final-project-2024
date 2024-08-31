package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

import com.synacy.graduate.program.leaveapp.leave_management.employee.Employee;
import com.synacy.graduate.program.leaveapp.leave_management.employee.EmployeeRepository;
import com.synacy.graduate.program.leaveapp.leave_management.employee.EmployeeRole;
import com.synacy.graduate.program.leaveapp.leave_management.employee.EmployeeService;
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Employee manager = employeeService.getEmployeeById(managerId)
                .orElseThrow(ResourceNotFoundException::new);

        if(manager.getRole() != EmployeeRole.MANAGER){
            throw new NotAManagerException();
        }

        return leaveApplicationRepository.findAllByManager(manager, pageable);
    }

    Page<LeaveApplication> getAllLeaveApplications(int max, int page) {
        Pageable pageable = PageRequest.of(page - 1, max, Sort.by("id"));
        return leaveApplicationRepository.findAll(pageable);
    }

    @Transactional
    LeaveApplication createLeaveApplication(
            Employee employee,
            CreateLeaveApplicationRequest createLeaveApplicationRequest
    ) throws InvalidLeaveDateException, InvalidLeaveApplicationException {
        Integer leaveWorkDays = calculateLeaveWorkDays(
                createLeaveApplicationRequest.getStartDate(),
                createLeaveApplicationRequest.getEndDate()
        );

        employeeService.subtractEmployeeAvailableLeaveCredits(employee, leaveWorkDays);

        LeaveApplication leaveApplication = setLeaveApplication(employee, createLeaveApplicationRequest, leaveWorkDays);

        return leaveApplicationRepository.save(leaveApplication);
    }

    private static LeaveApplication setLeaveApplication(Employee employee, CreateLeaveApplicationRequest createLeaveApplicationRequest, Integer leaveWorkDays) {
        LeaveApplication leaveApplication = new LeaveApplication();
        leaveApplication.setEmployee(employee);
        leaveApplication.setManager(employee.getManager());
        leaveApplication.setStartDate(createLeaveApplicationRequest.getStartDate());
        leaveApplication.setEndDate(createLeaveApplicationRequest.getEndDate());
        leaveApplication.setWorkDays(leaveWorkDays);
        leaveApplication.setReason(createLeaveApplicationRequest.getReason());
        leaveApplication.setStatus(LeaveApplicationStatus.PENDING);
        return leaveApplication;
    }

    private Integer calculateLeaveWorkDays(LocalDate startDate, LocalDate endDate) throws InvalidLeaveDateException {
        validateLeaveDates(startDate, endDate);

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

    private void validateLeaveDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidLeaveDateException("Start date or end date cannot be null.");
        } else if (startDate.isAfter(endDate)) {
            throw new InvalidLeaveDateException("Start date cannot be after end date.");
        } else if (startDate.isBefore(LocalDate.now()) || endDate.isBefore(LocalDate.now())) {
            throw new InvalidLeaveDateException("Start or end date cannot be before current date.");
        }
    }
}
