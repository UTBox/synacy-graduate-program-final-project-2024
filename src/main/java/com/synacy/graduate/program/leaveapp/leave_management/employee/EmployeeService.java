package com.synacy.graduate.program.leaveapp.leave_management.employee;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {

    public EmployeeService() {

    }

    public void updateEmployee(Optional<Employee> selectedEmployee, Optional<Integer> updatedTotalLeaveCredits) {
        /* TODO: Update exceptions being thrown once custom exceptions have been created.
            08/28/24 16:41
         */
        Employee employee = selectedEmployee.orElseThrow(RuntimeException::new);
        Integer totalLeaveCredits = updatedTotalLeaveCredits.orElseThrow(RuntimeException::new);
        employee.setTotalLeaves(totalLeaveCredits);

//        return new UpdateEmployeeResponse(employee);
    }
}
