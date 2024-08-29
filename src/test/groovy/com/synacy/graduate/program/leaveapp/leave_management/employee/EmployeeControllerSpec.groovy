package com.synacy.graduate.program.leaveapp.leave_management.employee

import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.InvalidOperationException
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.InvalidRequestException
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.ResourceNotFoundException
import org.springframework.data.domain.Sort
import spock.lang.Specification


class EmployeeControllerSpec extends Specification {
    EmployeeController employeeController
    EmployeeService employeeService = Mock()

    def setup(){
        employeeController = new EmployeeController(employeeService)
    }

    def "getManagers should return the first 10 list of managers"(){
        given:
        Long id1 = 1
        String firstName1 = "John"
        String lastName1 = "Doe"

        Long id2 = 2
        String firstName2 = "Juan"
        String lastName2 = "Doh"

        Employee manager1 = Mock()
        manager1.getId() >> id1
        manager1.getFirstName() >> firstName1
        manager1.getLastName() >> lastName1


        Employee manager2 = Mock()
        manager2.getId() >> id2
        manager2.getFirstName() >> firstName2
        manager2.getLastName() >> lastName2

        List<Employee> managersList = [manager1, manager2]

        when:
        List<ManagerResponse> managersResponse = employeeController.getManager()

        then:
        1 * employeeService.getManagers() >> managersList
        id1 == managersResponse[0].getId()
        firstName1 == managersResponse[0].getFirstName()
        lastName1 == managersResponse[0].getLastName()
        id2 == managersResponse[1].getId()
        firstName2 == managersResponse[1].getFirstName()
        lastName2 == managersResponse[1].getLastName()
    }

    def "createEmployee should throw an InvalidOperationException when creating an employee with an HR_ADMIN role"(){
        given:
        String errorCode = "HR_ADMIN_CREATION"
        String errorMessage = "Cannot create an HR Admin employee"

        CreateEmployeeRequest createEmployeeRequest = Mock()

        employeeService.createEmployee(createEmployeeRequest) >> {throw new InvalidOperationException(errorCode, errorMessage)}

        when:
        employeeController.createEmployee(createEmployeeRequest)

        then:
        InvalidOperationException e = thrown(InvalidOperationException)
        errorCode == e.errorCode
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

    def "createEmployee should throw an InvalidRequestException when the given employee associated with the manager ID cannot be a manager to the created employee"(){
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
}