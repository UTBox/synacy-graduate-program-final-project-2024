package com.synacy.graduate.program.leaveapp.leave_management.employee

import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.InvalidRequestException
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.ResourceNotFoundException
import spock.lang.Specification


class EmployeeControllerSpec extends Specification {
    EmployeeController employeeController
    EmployeeService employeeService = Mock()

    def setup(){
        employeeController = new EmployeeController(employeeService)
    }

    def "createEmployee should throw an InvalidRequestException when the provided manager ID does not exist"(){
        given:
        CreateEmployeeRequest createEmployeeRequest = Mock()

        employeeService.createEmployee(createEmployeeRequest) >> {throw new ResourceNotFoundException()}

        when:
        employeeController.createEmployee(createEmployeeRequest)

        then:
        thrown(InvalidRequestException)
    }

    def "createEmployee should throw an InvalidRequestException when the given employee associated with the manager ID is not a manager"(){
        given:
        CreateEmployeeRequest createEmployeeRequest = Mock()

        employeeService.createEmployee(createEmployeeRequest) >> {throw new NotManagerException()}

        when:
        employeeController.createEmployee(createEmployeeRequest)

        then:
        thrown(InvalidRequestException)
    }

    def "createEmployee should throw an InvalidRequestException when the employee to be created has an EMPLOYEE role but no manager is provided"(){
        given:
        CreateEmployeeRequest createEmployeeRequest = Mock()

        employeeService.createEmployee(createEmployeeRequest) >> {throw new NoManagerException()}

        when:
        employeeController.createEmployee(createEmployeeRequest)

        then:
        thrown(InvalidRequestException)
    }

    def "createEmployee should create and save an employee based on the given details"(){
        given:
        Long id = 2
        String firstName = "John"
        String lastName = "Dela Cruz"
        String employeeName = "John Dela Cruz"
        EmployeeRole role = EmployeeRole.EMPLOYEE
        Long managerId = 1
        Integer totalLeaves = 25
        Integer availableLeaves = 25

        CreateEmployeeRequest createEmployeeRequest = Mock()
        createEmployeeRequest.getFirstName() >> firstName
        createEmployeeRequest.getLastName() >>lastName
        createEmployeeRequest.getRole() >> role
        createEmployeeRequest.getManagerId() >> managerId
        createEmployeeRequest.getTotalLeaves() >> totalLeaves

        String managerFirstName = "Man"
        String managerLastName = "Ager"
        Employee manager = Mock()
        manager.getId() >> managerId
        manager.getFirstName() >> managerFirstName
        manager.getLastName() >> managerLastName

        and:
        Employee employee = Mock()
        employee.getId() >> id
        employee.getFirstName() >> firstName
        employee.getLastName() >> lastName
        employee.getRole() >> role
        employee.getManager() >> manager
        employee.getTotalLeaves() >> totalLeaves
        employee.getAvailableLeaves() >> availableLeaves

        employeeService.createEmployee(createEmployeeRequest) >> employee
        when:
        EmployeeResponse response = employeeController.createEmployee(createEmployeeRequest)

        then:
        id == response.getId()
        employeeName == response.getEmployeeName()
        role == response.getRole()
        totalLeaves == response.getTotalLeaves()
        availableLeaves == response.getAvailableLeaves()
        managerId == response.getManager().getId()
        managerFirstName == response.getManager().getFirstName()
        managerLastName == response.getManager().getLastName()
    }

    def "createEmployee should create and save an employee with no manager when provided with details without a manager"(){
        given:
        Long id = 2
        String firstName = "John"
        String lastName = "Dela Cruz"
        String employeeName = "John Dela Cruz"
        EmployeeRole role = EmployeeRole.EMPLOYEE
        Integer totalLeaves = 25
        Integer availableLeaves = 25

        CreateEmployeeRequest createEmployeeRequest = Mock()
        createEmployeeRequest.getFirstName() >> firstName
        createEmployeeRequest.getLastName() >> lastName
        createEmployeeRequest.getRole() >> role
        createEmployeeRequest.getManagerId() >> null
        createEmployeeRequest.getTotalLeaves() >> totalLeaves

        and:
        Employee employee = Mock()
        employee.getId() >> id
        employee.getFirstName() >> firstName
        employee.getLastName() >> lastName
        employee.getRole() >> role
        employee.getTotalLeaves() >> totalLeaves
        employee.getAvailableLeaves() >> availableLeaves

        employeeService.createEmployee(createEmployeeRequest) >> employee
        when:
        EmployeeResponse response = employeeController.createEmployee(createEmployeeRequest)

        then:
        id == response.getId()
        employeeName == response.getEmployeeName()
        role == response.getRole()
        totalLeaves == response.getTotalLeaves()
        availableLeaves == response.getAvailableLeaves()
        null == response.getManager()
    }
}