package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication

import com.synacy.graduate.program.leaveapp.leave_management.employee.Employee
import com.synacy.graduate.program.leaveapp.leave_management.employee.EmployeeService
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.ResourceNotFoundException
import spock.lang.Specification

class LeaveQuantityModifierSpec extends Specification {
    LeaveQuantityModifier leaveQuantityModifier
    EmployeeService employeeService = Mock(EmployeeService)

    def setup() {
        leaveQuantityModifier = new LeaveQuantityModifier(employeeService)
    }

    def "deductLeaveQuantityBasedOnLeaveWorkDays should throw an InvalidLeaveApplicationException when the employee's available leave days is less than the applied work leave days"() {
        given:
        Employee employee = new Employee(availableLeaves: 10)
        int leaveDays = 15

        when:
        leaveQuantityModifier.deductLeaveQuantityBasedOnLeaveWorkDays(employee, leaveDays)

        then:
        thrown(InvalidLeaveApplicationException)
    }

    def "deductLeaveQuantityBasedOnLeaveWorkDays should deduct the employee available leave balance with the given leaveDays"() {
        given:
        Employee employee = new Employee(availableLeaves: 15)
        int leaveDays = 5

        int expectedAvailableLeaves = 10

        when:
        leaveQuantityModifier.deductLeaveQuantityBasedOnLeaveWorkDays(employee, leaveDays)

        then:
        expectedAvailableLeaves == employee.availableLeaves
    }

    def "addLeaveQuantityBasedOnRejectedOrCancelledRequest should throw a ResourceNotFoundException when the employee id associated with the leave application does not exist"() {
        given:
        Long leaveId = 1
        Long employeeId = 3

        Employee mockEmployee = Mock(Employee) {
            id >> employeeId
        }

        LeaveApplication leaveApplication = Mock(LeaveApplication) {
            id >> leaveId
            employee >> mockEmployee
        }

        employeeService.getEmployeeById(employeeId) >> Optional.empty()

        when:
        leaveQuantityModifier.addLeaveQuantityBasedOnRejectedOrCancelledRequest(leaveApplication)

        then:
        thrown(ResourceNotFoundException)
    }

    def "addLeaveQuantityBasedOnRejectedOrCancelledRequest should add the employee available leave balance with the given leaveDays"() {
        given:
        Long leaveId = 1
        Long employeeId = 3
        int employeeAvailableLeaves = 10
        int leaveDays = 5
        int expectedAvailableLeaves = 15

        Employee theEmployee = new Employee(id: employeeId, availableLeaves: employeeAvailableLeaves)

        LeaveApplication leaveApplication = Mock(LeaveApplication) {
            id >> leaveId
            employee >> theEmployee
            workDays >> leaveDays
        }

        employeeService.getEmployeeById(employeeId) >> Optional.of(theEmployee)

        when:
        leaveQuantityModifier.addLeaveQuantityBasedOnRejectedOrCancelledRequest(leaveApplication)

        then:
        expectedAvailableLeaves == theEmployee.availableLeaves
    }
}
