package com.synacy.graduate.program.leaveapp.leave_management.employee

import spock.lang.Specification


class EmployeeServiceSpec extends Specification {
    EmployeeService employeeService;
    EmployeeRepository employeeRepository = Mock();

    def setup(){
        employeeService = new EmployeeService(employeeRepository)
    }

    def "createEmployee should create and save an employee based on the given details"(){
        given:
        String firstName = "John"
        String lastName = "Dela Cruz"
        EmployeeRole role = EmployeeRole.EMPLOYEE
        Long managerId = 1
        Integer totalLeaves = 25
        Integer availableLeaves = 25
        Boolean isDeleted = false

        CreateEmployeeRequest createEmployeeRequest = Mock()
        createEmployeeRequest.getFirstName() >> firstName
        createEmployeeRequest.getLastName() >>lastName
        createEmployeeRequest.getRole() >> role
        createEmployeeRequest.getManagerId() >> managerId
        createEmployeeRequest.getTotalLeaves() >> totalLeaves

        Employee manager = Mock()
        employeeRepository.findById(managerId) >> Optional.of(manager)
        when:
        employeeService.createEmployee(createEmployeeRequest)

        then:
        1 * employeeRepository.save(_ as Employee) >> {Employee employee ->
            assert firstName == employee.getFirstName()
            assert lastName == employee.getLastName()
            assert role == employee.getRole()
            assert totalLeaves == employee.getTotalLeaves()
            assert availableLeaves == employee.getAvailableLeaves()
            assert manager == employee.getManager()
            assert isDeleted == employee.getIsDeleted()
        }
    }

    def "createEmployee should create and save an employee with no manager when provided with details without a manager"(){
        given:
        String firstName = "John"
        String lastName = "Dela Cruz"
        EmployeeRole role = EmployeeRole.EMPLOYEE
        Integer totalLeaves = 25
        Integer availableLeaves = 25
        Boolean isDeleted = false

        CreateEmployeeRequest createEmployeeRequest = Mock()
        createEmployeeRequest.getFirstName() >> firstName
        createEmployeeRequest.getLastName() >>lastName
        createEmployeeRequest.getRole() >> role
        createEmployeeRequest.getManagerId() >> null
        createEmployeeRequest.getTotalLeaves() >> totalLeaves

        when:
        employeeService.createEmployee(createEmployeeRequest)

        then:
        1 * employeeRepository.save(_) >> {Employee employee ->
            assert firstName == employee.getFirstName()
            assert lastName == employee.getLastName()
            assert role == employee.getRole()
            assert totalLeaves == employee.getTotalLeaves()
            assert availableLeaves == employee.getAvailableLeaves()
            assert null == employee.getManager()
            assert isDeleted == employee.getIsDeleted()
        }
    }

}