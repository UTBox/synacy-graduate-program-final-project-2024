package com.synacy.graduate.program.leaveapp.leave_management.employee

import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.ResourceNotFoundException
import spock.lang.Specification


class EmployeeServiceSpec extends Specification {
    EmployeeService employeeService;
    List<Employee> employeesList = Mock();
    EmployeeRepository employeeRepository = Mock();

    def setup(){
        employeeService = new EmployeeService(employeesList, employeeRepository)
    }

    def "createEmployee should throw a ResourceNotFoundException when the provided manager ID does not exist"(){
        given:
        String firstName = "John"
        String lastName = "Dela Cruz"
        EmployeeRole role = EmployeeRole.EMPLOYEE
        Long managerId = 1
        Integer totalLeaves = 25

        CreateEmployeeRequest createEmployeeRequest = Mock()
        createEmployeeRequest.getFirstName() >> firstName
        createEmployeeRequest.getLastName() >>lastName
        createEmployeeRequest.getRole() >> role
        createEmployeeRequest.getManagerId() >> managerId
        createEmployeeRequest.getTotalLeaves() >> totalLeaves

        employeeRepository.findById(managerId) >> Optional.empty()

        when:
        employeeService.createEmployee(createEmployeeRequest)

        then:
        thrown(ResourceNotFoundException)
    }

    def "createEmployee should throw a NotManagerException when the given employee associated with the manager ID is not a manager"(){
        given:
        String firstName = "John"
        String lastName = "Dela Cruz"
        EmployeeRole role = EmployeeRole.EMPLOYEE
        Long managerId = 1
        Integer totalLeaves = 25

        CreateEmployeeRequest createEmployeeRequest = Mock()
        createEmployeeRequest.getFirstName() >> firstName
        createEmployeeRequest.getLastName() >>lastName
        createEmployeeRequest.getRole() >> role
        createEmployeeRequest.getManagerId() >> managerId
        createEmployeeRequest.getTotalLeaves() >> totalLeaves

        Employee manager = Mock()
        manager.getRole() >> managerRole

        employeeRepository.findById(managerId) >> Optional.of(manager)

        when:
        employeeService.createEmployee(createEmployeeRequest)

        then:
        thrown(NotManagerException)

        where:
        managerRole << [EmployeeRole.EMPLOYEE, EmployeeRole.HR_ADMIN]
    }

    def "createEmployee should throw a ManagerNullException when the employee with an EMPLOYEE role has no manager provided"(){
        given:
        String firstName = "John"
        String lastName = "Dela Cruz"
        EmployeeRole role = EmployeeRole.EMPLOYEE
        Integer totalLeaves = 25

        CreateEmployeeRequest createEmployeeRequest = Mock()
        createEmployeeRequest.getFirstName() >> firstName
        createEmployeeRequest.getLastName() >>lastName
        createEmployeeRequest.getRole() >> role
        createEmployeeRequest.getManagerId() >> null
        createEmployeeRequest.getTotalLeaves() >> totalLeaves

        when:
        employeeService.createEmployee(createEmployeeRequest)

        then:
        thrown(NoManagerException)
    }

    def "createEmployee should create and save an employee with #role role based on the given employee details and manager ID"(){
        given:
        String firstName = "John"
        String lastName = "Dela Cruz"
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
        manager.getRole() >> EmployeeRole.MANAGER
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

        where:
        role << [EmployeeRole.MANAGER, EmployeeRole.HR_ADMIN, EmployeeRole.EMPLOYEE]
    }

    def "createEmployee should create and save an employee with a #role role and no manager when provided with details without a manager"(){
        given:
        String firstName = "John"
        String lastName = "Dela Cruz"
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
        where:
        role << [EmployeeRole.MANAGER, EmployeeRole.HR_ADMIN]
    }
}