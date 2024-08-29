package com.synacy.graduate.program.leaveapp.leave_management.employee

import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.InvalidOperationException
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

    def "updateEmployee should call EmployeeService updateEmployee and return an EmployeeResponse with updated employee totalLeaves"() {
        given:
        Long id = 1
        Integer updatedTotalLeaves = 20
        String firstName = "John"
        String lastName = "Doe"
        EmployeeRole role = EmployeeRole.EMPLOYEE
        Employee manager = Mock()
        Integer totalLeaves = 15
        Integer availableLeaves = 12
        Boolean isDeleted = false

        UpdateEmployeeRequest updateEmployeeRequest = Mock()

        Employee employee = Mock()
        employee.getId() >> id
        employee.getTotalLeaves() >> totalLeaves

        employeeService.getEmployeeById(id) >> Optional.of(employee)

        Employee updatedEmployee = Mock()
        updatedEmployee.getId() >> id
        updatedEmployee.getFirstName() >> firstName
        updatedEmployee.getLastName() >> lastName
        updatedEmployee.getRole() >> role
        updatedEmployee.getTotalLeaves() >> updatedTotalLeaves
        updatedEmployee.getAvailableLeaves() >> availableLeaves
        updatedEmployee.getManager() >> manager
        updatedEmployee.getIsDeleted() >> isDeleted

        when:
        EmployeeResponse employeeResponse = employeeController.updateEmployee(id, updateEmployeeRequest)

        then:
        1 * employeeService.updateEmployee(employee, updateEmployeeRequest) >> updatedEmployee
        id == employeeResponse.getId()
        (firstName + " " + lastName) == employeeResponse.getEmployeeName()
        role == employeeResponse.getRole()
        updatedTotalLeaves == employeeResponse.getTotalLeaves()
        availableLeaves == employeeResponse.getAvailableLeaves()
    }

    def "updateEmployee should throw ResourceNotFoundException if employee id is not found"() {
        given:
        Long id = 5L
        UpdateEmployeeRequest updateEmployeeRequest = Mock()

        employeeService.getEmployeeById(id) >> Optional.empty()

        when:
        employeeController.updateEmployee(id, updateEmployeeRequest)

        then:
        thrown(ResourceNotFoundException)
    }

    def "updateEmployee should throw InvalidOperationException if updatedTotalLeaves is less than availableLeaves"() {
        given:
        Long id = 1L
        Integer updatedTotalLeaves = 15
        Integer availableLeaves = 25

        UpdateEmployeeRequest updateEmployeeRequest = Mock()
        updateEmployeeRequest.getTotalLeaveCredits() >> updatedTotalLeaves

        Employee employee = Mock()
        employee.getAvailableLeaves() >> availableLeaves

        employeeService.getEmployeeById(id) >> Optional.of(employee)
        employeeService.updateEmployee(employee, updateEmployeeRequest) >> { throw new InvalidUpdatedTotalLeavesException() }

        when:
        employeeController.updateEmployee(id, updateEmployeeRequest)

        then:
        thrown(InvalidOperationException)
    }
}