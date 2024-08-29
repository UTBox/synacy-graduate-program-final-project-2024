package com.synacy.graduate.program.leaveapp.leave_management.employee

import com.synacy.graduate.program.leaveapp.leave_management.web.PageResponse
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.InvalidRequestException
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.ResourceNotFoundException
import org.springframework.data.domain.Page
import spock.lang.Specification


class EmployeeControllerSpec extends Specification {
    EmployeeController employeeController
    EmployeeService employeeService = Mock()

    def setup() {
        employeeController = new EmployeeController(employeeService)
    }

    def "getEmployees should return a paged response of employees when max and page parameters are valid"() {
        given:
        int max = 2
        int page = 1
        int totalCount = 1

        Employee employee = Mock(Employee) {
            id >> 1L
            firstName >> "John"
            lastName >> "Wick"
            role >> EmployeeRole.EMPLOYEE
            totalLeaves >> 15
            availableLeaves >> 15
        }

        List<Employee> employeesList = [employee]
        Page<Employee> paginatedEmployees = Mock(Page) {
            content >> employeesList
            totalElements >> totalCount
        }

        employeeService.getEmployees(max, page) >> paginatedEmployees

        when:
        PageResponse<EmployeeResponse> result = employeeController.getEmployees(max, page)

        then:
        totalCount == result.totalCount()
        page == result.pageNumber()
        employeesList[0].id == result.content()[0].id
        employeesList[0].firstName == result.content()[0].firstName
        employeesList[0].lastName == result.content()[0].lastName
        employeesList[0].role == result.content()[0].role
        employeesList[0].totalLeaves == result.content()[0].totalLeaves
        employeesList[0].availableLeaves == result.content()[0].availableLeaves
    }

    def "getEmployee should throw a ResourceNotFoundException when employee of given id does not exist"() {
        given:
        Long id = 1
        employeeService.getEmployeeById(id) >> Optional.empty()

        when:
        employeeController.getEmployee(id)

        then:
        thrown(ResourceNotFoundException)
    }

    def "getEmployee should return an employee when employee of given id exists"() {
        given:
        Long employeeId = 1
        Employee employee = Mock(Employee) {
            id >> employeeId
            firstName >> "John"
            lastName >> "Wick"
            role >> EmployeeRole.EMPLOYEE
            totalLeaves >> 15
            availableLeaves >> 15
        }

        employeeService.getEmployeeById(employeeId) >> Optional.of(employee)

        when:
        EmployeeResponse result = employeeController.getEmployee(employeeId)

        then:
        employee.id == result.id
        employee.firstName == result.firstName
        employee.lastName == result.lastName
        employee.role == result.role
        employee.totalLeaves == result.totalLeaves
        employee.availableLeaves == result.availableLeaves
    }

    def "createEmployee should throw an InvalidRequestException when the provided manager ID does not exist"() {
        given:
        CreateEmployeeRequest createEmployeeRequest = Mock()

        employeeService.createEmployee(createEmployeeRequest) >> { throw new ResourceNotFoundException() }

        when:
        employeeController.createEmployee(createEmployeeRequest)

        then:
        thrown(InvalidRequestException)
    }

    def "createEmployee should throw an InvalidRequestException when the given employee associated with the manager ID is not a manager"() {
        given:
        CreateEmployeeRequest createEmployeeRequest = Mock()

        employeeService.createEmployee(createEmployeeRequest) >> { throw new NotManagerException() }

        when:
        employeeController.createEmployee(createEmployeeRequest)

        then:
        thrown(InvalidRequestException)
    }

    def "createEmployee should throw an InvalidRequestException when the employee to be created has an EMPLOYEE role but no manager is provided"() {
        given:
        CreateEmployeeRequest createEmployeeRequest = Mock()

        employeeService.createEmployee(createEmployeeRequest) >> { throw new NoManagerException() }

        when:
        employeeController.createEmployee(createEmployeeRequest)

        then:
        thrown(InvalidRequestException)
    }

    def "createEmployee should create and save an employee based on the given details"() {
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
        createEmployeeRequest.getLastName() >> lastName
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

    def "createEmployee should create and save an employee with no manager when provided with details without a manager"() {
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