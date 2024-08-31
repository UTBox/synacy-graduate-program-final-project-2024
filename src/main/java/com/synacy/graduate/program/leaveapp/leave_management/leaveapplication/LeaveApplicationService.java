package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

import com.synacy.graduate.program.leaveapp.leave_management.employee.Employee;
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
import java.util.Optional;

@Service
public class LeaveApplicationService {

    private final LeaveApplicationRepository leaveApplicationRepository;
    private final EmployeeService employeeService;
    private final LeaveQuantityModifier leaveQuantityModifier;

    @Autowired
    public LeaveApplicationService(
            LeaveApplicationRepository leaveApplicationRepository,
            EmployeeService employeeService,
            LeaveQuantityModifier leaveQuantityModifier) {
        this.leaveApplicationRepository = leaveApplicationRepository;
        this.employeeService = employeeService;
        this.leaveQuantityModifier = leaveQuantityModifier;
    }

    Page<LeaveApplication> getLeavesByManager(int max, int page, Long managerId) {
        Pageable pageable = PageRequest.of(page - 1, max, Sort.by("id"));
        Employee manager = employeeService.getEmployeeById(managerId)
                .orElseThrow(ResourceNotFoundException::new);

        if (manager.getRole() != EmployeeRole.MANAGER) {
            throw new NotAManagerException();
        }

        return leaveApplicationRepository.findAllByManager(manager, pageable);
    }

    Page<LeaveApplication> getAllLeaveApplications(int max, int page) {
        Pageable pageable = PageRequest.of(page - 1, max, Sort.by("id"));
        return leaveApplicationRepository.findAll(pageable);
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

        while (!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                leaveWorkDays++;
            }
            currentDate = currentDate.plusDays(1);
        }

        return leaveWorkDays;
    }

    @Transactional
    LeaveApplication updateLeaveApplication(LeaveApplication leave, UpdateLeaveApplicationRequest request) {
        if (leave.getStatus() != LeaveApplicationStatus.PENDING) {
            throw new InvalidLeaveApplicationStatusException("Leave application status is not PENDING.");
        }

        if (request.getLeaveApplicationStatus() == LeaveApplicationStatus.APPROVED
                || request.getLeaveApplicationStatus() == LeaveApplicationStatus.REJECTED) {
            leave.setStatus(request.getLeaveApplicationStatus());
            updateLeaveQuantityBasedOnStatus(leave);
        }

        return leaveApplicationRepository.save(leave);
    }

    Optional<LeaveApplication> getLeaveApplicationById(Long id) {
        return leaveApplicationRepository.findById(id);
    }

    private void updateLeaveQuantityBasedOnStatus(LeaveApplication leave) {
        switch (leave.getStatus()) {
            case APPROVED:
                leaveQuantityModifier.deductLeaveQuantityBasedOnApprovedRequest(leave);
                break;
            case REJECTED:
            case CANCELLED:
                leaveQuantityModifier.addLeaveQuantityBasedOnRejectedOrCancelledRequest(leave);
                break;
            default:
                break;
        }
    }
}
