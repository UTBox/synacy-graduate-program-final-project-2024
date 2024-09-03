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
            LeaveQuantityModifier leaveQuantityModifier
    ) {
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

    Page<LeaveApplication> getLeavesByEmployee(int max, int page, Long employeeId) {
        Pageable pageable = PageRequest.of(page - 1, max, Sort.by("id"));
        Employee employee = employeeService.getEmployeeById(employeeId)
                .orElseThrow(ResourceNotFoundException::new);
        return leaveApplicationRepository.findAllByEmployee(employee, pageable);
    }

    Page<LeaveApplication> getPendingLeaveApplications(int max, int page) {
        Pageable pageable = PageRequest.of(page - 1, max, Sort.by("id"));
        return leaveApplicationRepository.findAllByStatus(LeaveApplicationStatus.PENDING, pageable);
    }

    Optional<LeaveApplication> getLeaveApplicationById(Long id) {
        return leaveApplicationRepository.findById(id);
    }

    @Transactional
    LeaveApplication createLeaveApplication(CreateLeaveApplicationRequest createLeaveApplicationRequest)
            throws InvalidLeaveDateException, InvalidLeaveApplicationException {

        Employee employee = employeeService
                .getEmployeeById(createLeaveApplicationRequest.getEmployeeId())
                .orElseThrow(ResourceNotFoundException::new);

        Integer leaveWorkDays = calculateLeaveWorkDays(
                employee.getId(),
                createLeaveApplicationRequest.getStartDate(),
                createLeaveApplicationRequest.getEndDate()
        );

        LeaveApplication leaveApplication = setLeaveApplication(employee, createLeaveApplicationRequest, leaveWorkDays);
        leaveQuantityModifier.deductLeaveQuantityBasedOnLeaveWorkDays(employee, leaveWorkDays);

        return leaveApplicationRepository.save(leaveApplication);
    }

    @Transactional
    LeaveApplication updateLeaveApplication(LeaveApplication leave, UpdateLeaveApplicationRequest request) {
        if (leave.getStatus() != LeaveApplicationStatus.PENDING) {
            throw new StatusNotPendingException("Leave application status must be PENDING to update.");
        }

        switch (request.getStatus()) {
            case REJECTED:
                leaveQuantityModifier.addLeaveQuantityBasedOnRejectedOrCancelledRequest(leave);
                leave.setStatus(LeaveApplicationStatus.REJECTED);
                break;

            case APPROVED:
                leave.setStatus(LeaveApplicationStatus.APPROVED);
                break;

            case CANCELLED:
                throw new InvalidLeaveApplicationException("Cancellation requests are not allowed in this method.");
        }

        return leaveApplicationRepository.save(leave);
    }

    @Transactional
    void cancelLeaveApplication(LeaveApplication leave) {
        if (leave.getStatus() != LeaveApplicationStatus.PENDING) {
            throw new StatusNotPendingException("Leave application status is not PENDING.");
        }
        leave.cancelLeave();
        leaveQuantityModifier.addLeaveQuantityBasedOnRejectedOrCancelledRequest(leave);
        leaveApplicationRepository.save(leave);
    }

    private static LeaveApplication setLeaveApplication(
            Employee employee,
            CreateLeaveApplicationRequest createLeaveApplicationRequest,
            Integer leaveWorkDays) {

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

    private Integer calculateLeaveWorkDays(Long employeeId, LocalDate startDate, LocalDate endDate)
            throws InvalidLeaveDateException {

        validateLeaveDates(employeeId, startDate, endDate);

        int leaveWorkDays = 0;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                leaveWorkDays++;
            }
            currentDate = currentDate.plusDays(1);
        }

        if (leaveWorkDays == 0) {
            throw new InvalidLeaveDateException("Invalid leave dates set.");
        }

        return leaveWorkDays;
    }

    private void validateLeaveDates(Long employeeId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidLeaveDateException("Start date or end date cannot be null.");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidLeaveDateException("Start date cannot be after end date.");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new InvalidLeaveDateException("Start date cannot be before current date.");
        }
        if (leaveApplicationRepository.countOverlappingLeaveApplications(employeeId, startDate, endDate) > 0) {
            throw new InvalidLeaveDateException("Overlapping leave applications.");
        }
    }
}
