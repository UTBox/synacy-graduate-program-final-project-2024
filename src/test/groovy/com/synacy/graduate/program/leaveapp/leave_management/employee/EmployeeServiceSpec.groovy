package com.synacy.graduate.program.leaveapp.leave_management.employee

import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.InvalidOperationException
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.ResourceNotFoundException
import org.springframework.data.domain.Page
import spock.lang.Specification


class EmployeeServiceSpec extends Specification {
    EmployeeService employeeService;
    List<Employee> employeesList = Mock();
    EmployeeRepository employeeRepository = Mock();

    def setup(){
        employeeService = new EmployeeService(employeesList, employeeRepository)
    }

    def "getEmployees should return a page of non-deleted employees given max and page number"() {
        given:
        Employee employee = Mock(Employee) {
            id >> 1L
            firstName >> "John"
            lastName >> "Doe"
            role >> EmployeeRole.EMPLOYEE
            totalLeaves >> 15
            availableLeaves >> 15
        }
        List<Employee> employeesList = [employee]
        Page<Employee> paginatedEmployees = Mock(Page)
        paginatedEmployees.content >> employeesList

        int max = 2
        int page = 1

        when:
        Page<Employee> result = employeeService.getEmployees(max, page)

        then:
        1 * employeeRepository.findAllByIsDeletedIsFalse(_) >> paginatedEmployees
        employeesList[0].id == result.content[0].id
        employeesList[0].firstName == result.content[0].firstName
        employeesList[0].lastName == result.content[0].lastName
        employeesList[0].role == result.content[0].role
        employeesList[0].totalLeaves == result.content[0].totalLeaves
        employeesList[0].availableLeaves == result.content[0].availableLeaves
    }

    def "getEmployeeById should return an employee with the given id"() {
        given:
        Long id = 1

        Employee employee = Mock(Employee)
        employee.id >> id
        employee.firstName >> "John"
        employee.lastName >> "Doe"
        employee.role >> EmployeeRole.EMPLOYEE
        employee.totalLeaves >> 15

        when:
        Optional<Employee> result = employeeService.getEmployeeById(id)

        then:
        1 * employeeRepository.findById(id) >> Optional.of(employee)
        employee == result.get()
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