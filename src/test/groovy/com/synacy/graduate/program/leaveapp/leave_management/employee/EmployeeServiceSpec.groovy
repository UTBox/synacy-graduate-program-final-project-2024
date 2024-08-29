package com.synacy.graduate.program.leaveapp.leave_management.employee

import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.InvalidOperationException
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.ResourceNotFoundException
import org.springframework.data.domain.Sort
import spock.lang.Specification


class EmployeeServiceSpec extends Specification {
    EmployeeService employeeService;
    List<Employee> employeesList = Mock();
    EmployeeRepository employeeRepository = Mock();

    def setup(){
        employeeService = new EmployeeService(employeesList, employeeRepository)
    }

    def "getManagers should return the first 10 list of managers"(){
        given:
        Long id1 = 1
        Long id2 = 2

        Employee manager1 = Mock()
        manager1.getId() >> id1

        Employee manager2 = Mock()
        manager2.getId() >> id2

        List<Employee> managersList = [manager1, manager2]

        when:
        List<Employee> managersResponse = employeeService.getManagers();

        then:
        1 * employeeRepository.findFirst10Managers() >> managersList
        managersList == managersResponse
    }

    def "getManagersByName should return the first 10 list of managers with names that match the given filter"(){
        given:
        String nameFilter = "John"

        Long id1 = 1
        Long id2 = 2

        Employee manager1 = Mock()
        manager1.getId() >> id1

        Employee manager2 = Mock()
        manager2.getId() >> id2

        List<Employee> managersList = [manager1, manager2]

        when:
        List<Employee> managersResponse = employeeService.getManagersByName(nameFilter);

        then:
        1 * employeeRepository.findFirst10ManagersByName(nameFilter) >> managersList
        managersList == managersResponse
    }

    def "createEmployee should throw an InvalidOperationException when creating an HR Admin employee"(){
        given:
        String firstName = "John"
        String lastName = "Dela Cruz"
        EmployeeRole role = EmployeeRole.HR_ADMIN
        Integer totalLeaves = 25

        CreateEmployeeRequest createEmployeeRequest = Mock()
        createEmployeeRequest.getFirstName() >> firstName
        createEmployeeRequest.getLastName() >>lastName
        createEmployeeRequest.getRole() >> role
        createEmployeeRequest.getTotalLeaves() >> totalLeaves

        when:
        employeeService.createEmployee(createEmployeeRequest)

        then:
        thrown(InvalidOperationException)
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

        employeeRepository.findByIdAndIsDeletedIsFalse(managerId) >> Optional.empty()

        when:
        employeeService.createEmployee(createEmployeeRequest)

        then:
        thrown(ResourceNotFoundException)
    }

    def "createEmployee should throw a NotManagerException when assigning a manager with #managerRole role to an employee with #employeeRole role"(){
        given:
        String firstName = "John"
        String lastName = "Dela Cruz"
        Long managerId = 1
        Integer totalLeaves = 25

        CreateEmployeeRequest createEmployeeRequest = Mock()
        createEmployeeRequest.getFirstName() >> firstName
        createEmployeeRequest.getLastName() >> lastName
        createEmployeeRequest.getRole() >> employeeRole
        createEmployeeRequest.getManagerId() >> managerId
        createEmployeeRequest.getTotalLeaves() >> totalLeaves

        Employee manager = Mock()
        manager.getRole() >> managerRole

        employeeRepository.findByIdAndIsDeletedIsFalse(managerId) >> Optional.of(manager)

        when:
        employeeService.createEmployee(createEmployeeRequest)

        then:
        thrown(NotManagerException)

        where:
        employeeRole           | managerRole
        EmployeeRole.MANAGER   | EmployeeRole.EMPLOYEE
        EmployeeRole.EMPLOYEE  | EmployeeRole.EMPLOYEE
        EmployeeRole.EMPLOYEE  | EmployeeRole.HR_ADMIN

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

    def "createEmployee should create and save an employee with #employeeRole role based on the given employee details and manager with role #managerRole"(){
        given:
        String firstName = "John"
        String lastName = "Dela Cruz"
        Long managerId = 1
        Integer totalLeaves = 25
        Integer availableLeaves = 25
        Boolean isDeleted = false

        CreateEmployeeRequest createEmployeeRequest = Mock()
        createEmployeeRequest.getFirstName() >> firstName
        createEmployeeRequest.getLastName() >> lastName
        createEmployeeRequest.getRole() >> employeeRole
        createEmployeeRequest.getManagerId() >> managerId
        createEmployeeRequest.getTotalLeaves() >> totalLeaves

        Employee manager = Mock()
        manager.getRole() >> managerRole
        employeeRepository.findByIdAndIsDeletedIsFalse(managerId) >> Optional.of(manager)

        when:
        employeeService.createEmployee(createEmployeeRequest)

        then:
        1 * employeeRepository.save(_ as Employee) >> {Employee employee ->
            assert firstName == employee.getFirstName()
            assert lastName == employee.getLastName()
            assert employeeRole == employee.getRole()
            assert totalLeaves == employee.getTotalLeaves()
            assert availableLeaves == employee.getAvailableLeaves()
            assert manager == employee.getManager()
            assert isDeleted == employee.getIsDeleted()
        }

        where:
        employeeRole           | managerRole
        EmployeeRole.MANAGER   | EmployeeRole.MANAGER
        EmployeeRole.MANAGER   | EmployeeRole.HR_ADMIN
        EmployeeRole.EMPLOYEE  | EmployeeRole.MANAGER

    }

    def "createEmployee should create and save a MANAGER employee with a manager with HR_ADMIN role when given an empty manager ID"(){
        given:
        String firstName = "John"
        String lastName = "Dela Cruz"
        Integer totalLeaves = 25
        Integer availableLeaves = 25
        Boolean isDeleted = false

        CreateEmployeeRequest createEmployeeRequest = Mock()
        createEmployeeRequest.getFirstName() >> firstName
        createEmployeeRequest.getLastName() >> lastName
        createEmployeeRequest.getRole() >> EmployeeRole.MANAGER
        createEmployeeRequest.getTotalLeaves() >> totalLeaves

        Long managerId = 1
        Employee manager = Mock()
        manager.getId() >> managerId
        manager.getRole() >> EmployeeRole.HR_ADMIN
        employeeRepository.findByIdAndIsDeletedIsFalse(managerId) >> Optional.of(manager)

        when:
        employeeService.createEmployee(createEmployeeRequest)

        then:
        1 * employeeRepository.save(_ as Employee) >> {Employee employee ->
            assert firstName == employee.getFirstName()
            assert lastName == employee.getLastName()
            assert EmployeeRole.MANAGER == employee.getRole()
            assert totalLeaves == employee.getTotalLeaves()
            assert availableLeaves == employee.getAvailableLeaves()
            assert manager == employee.getManager()
            assert isDeleted == employee.getIsDeleted()
        }
    }
}