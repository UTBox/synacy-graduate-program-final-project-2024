package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

import com.synacy.graduate.program.leaveapp.leave_management.employee.Employee;
import com.synacy.graduate.program.leaveapp.leave_management.employee.EmployeeService;
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeaveQuantityModifier {
    private final EmployeeService employeeService;

    @Autowired
    public LeaveQuantityModifier(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public void deductLeaveQuantityBasedOnLeaveRequest(LeaveApplication leaveApplication) {
        Employee employee = employeeService
                .getEmployeeById(leaveApplication.getEmployee().getId())
                .orElseThrow(ResourceNotFoundException::new);

        employee.deductLeaveBalance(leaveApplication.getWorkDays());
    }

    public void addLeaveQuantityBasedOnRejectedOrCancelledRequest(LeaveApplication leaveApplication) {
        Employee employee = employeeService
                .getEmployeeById(leaveApplication.getEmployee().getId())
                .orElseThrow(ResourceNotFoundException::new);

        employee.addLeaveBalance(leaveApplication.getWorkDays());
    }
}
