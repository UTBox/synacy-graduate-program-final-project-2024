package com.synacy.graduate.program.leaveapp.leave_management.employee

import com.synacy.graduate.program.leaveapp.leave_management.web.PageResponse
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.InvalidOperationException
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

    def "getPaginatedEmployees should return a paged response of employees when the max and page parameters are valid"() {
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

        employeeService.getPaginatedEmployees(max, page) >> paginatedEmployees

        when:
        PageResponse<EmployeeResponse> result = employeeController.getPaginatedEmployees(max, page)

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

    def "getListEmployees should return a list of the first 10 employees"() {
        given:
        Long id1 = 1
        String firstName1 = "John"
        String lastName1 = "Doe"
        String name1 = "John Doe"
        EmployeeRole role1 = EmployeeRole.MANAGER

        Long id2 = 2
        String firstName2 = "Johns"
        String lastName2 = "Does"
        String name2 = "Johns Does"
        EmployeeRole role2 = EmployeeRole.EMPLOYEE

        Employee employee1 = Mock(Employee) {
            id >> id1
            firstName >> firstName1
            lastName >> lastName1
            role >> role1
        }
        employee1.getName() >> name1

        Employee employee2 = Mock(Employee) {
            id >> id2
            firstName >> firstName2
            lastName >> lastName2
            role >> role2
        }
        employee2.getName() >> name2

        List<Employee> employeeList = [employee1, employee2]
        employeeService.getListEmployees() >> employeeList

        when:
        List<EmployeeListResponse> response = employeeController.getListEmployees(null)

        then:
        id1 == response[0].getId()
        name1 == response[0].getName()
        role1 == response[0].getRole()

        id2 == response[1].getId()
        name2 == response[1].getName()
        role2 == response[1].getRole()
    }

    def "getListEmployees should return a list if the first 10 employees with names that matches the given name"() {
        given:
        String nameFilter = "John"

        Long id1 = 1
        String firstName1 = "John"
        String lastName1 = "Doe"
        String name1 = "John Doe"
        EmployeeRole role1 = EmployeeRole.MANAGER

        Long id2 = 2
        String firstName2 = "Johns"
        String lastName2 = "Does"
        String name2 = "Johns Does"
        EmployeeRole role2 = EmployeeRole.EMPLOYEE

        Employee employee1 = Mock(Employee) {
            id >> id1
            firstName >> firstName1
            lastName >> lastName1
            role >> role1
        }
        employee1.getName() >> name1

        Employee employee2 = Mock(Employee) {
            id >> id2
            firstName >> firstName2
            lastName >> lastName2
            role >> role2
        }
        employee2.getName() >> name2

        List<Employee> employeeList = [employee1, employee2]
        employeeService.getListEmployeesByName(nameFilter) >> employeeList

        when:
        List<EmployeeListResponse> response = employeeController.getListEmployees(nameFilter)

        then:
        id1 == response[0].getId()
        name1 == response[0].getName()
        role1 == response[0].getRole()

        id2 == response[1].getId()
        name2 == response[1].getName()
        role2 == response[1].getRole()
    }

    def "getListManagers should return the first 10 list of managers"() {
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
        List<ManagerResponse> managersResponse = employeeController.getListManagers(null)

        then:
        1 * employeeService.getListManagers() >> managersList
        id1 == managersResponse[0].getId()
        firstName1 == managersResponse[0].getFirstName()
        lastName1 == managersResponse[0].getLastName()
        id2 == managersResponse[1].getId()
        firstName2 == managersResponse[1].getFirstName()
        lastName2 == managersResponse[1].getLastName()
    }

    def "getListManagers should return the first 10 list of managers with names containing the filter name"() {
        given:
        String nameFilter = "John"

        Long id1 = 1
        String firstName1 = "John"
        String lastName1 = "Doe"

        Long id2 = 2
        String firstName2 = "John"
        String lastName2 = "Deer"

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
        List<ManagerResponse> managersResponse = employeeController.getListManagers(nameFilter)

        then:
        1 * employeeService.getListManagersByName(nameFilter) >> managersList
        id1 == managersResponse[0].getId()
        firstName1 == managersResponse[0].getFirstName()
        lastName1 == managersResponse[0].getLastName()
        id2 == managersResponse[1].getId()
        firstName2 == managersResponse[1].getFirstName()
        lastName2 == managersResponse[1].getLastName()
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

    def "createEmployee should throw an InvalidOperationException when creating an employee with an HR_ADMIN role"() {
        given:
        String errorCode = "HR_ADMIN_CREATION"
        String errorMessage = "Cannot create an HR Admin employee"

        CreateEmployeeRequest createEmployeeRequest = Mock()

        employeeService.createEmployee(createEmployeeRequest) >> { throw new InvalidOperationException(errorCode, errorMessage) }

        when:
        employeeController.createEmployee(createEmployeeRequest)

        then:
        InvalidOperationException e = thrown(InvalidOperationException)
        errorCode == e.errorCode
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

    def "createEmployee should throw an InvalidRequestException when the given employee associated with the manager ID cannot be a manager to the created employee"() {
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
        firstName == response.getFirstName()
        lastName == response.getLastName()
        employeeName == response.getFullName()
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
        firstName == response.getFirstName()
        lastName == response.getLastName()
        employeeName == response.getFullName()
        role == response.getRole()
        totalLeaves == response.getTotalLeaves()
        availableLeaves == response.getAvailableLeaves()
        null == response.getManager()
    }

    def "updateEmployee should call EmployeeService updateEmployee and return an EmployeeResponse with updated employee totalLeaves"() {
        given:
        Long id = 1
        Integer updatedTotalLeaves = 20
        Integer updatedAvailableLeaves = 17
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
        employee.getAvailableLeaves() >> availableLeaves

        Employee updatedEmployee = Mock() {
            getId() >> id
            getFirstName() >> firstName
            getLastName() >> lastName
            getRole() >> role
            getTotalLeaves() >> updatedTotalLeaves
            getAvailableLeaves() >> updatedAvailableLeaves
            getManager() >> manager
            getIsDeleted() >> isDeleted
        }

        when:
        EmployeeResponse employeeResponse = employeeController.updateEmployee(id, updateEmployeeRequest)

        then:
        1 * employeeService.updateEmployee(id, updateEmployeeRequest) >> updatedEmployee
        id == employeeResponse.getId()
        firstName == employeeResponse.getFirstName()
        lastName == employeeResponse.getLastName()
        role == employeeResponse.getRole()
        updatedTotalLeaves == employeeResponse.getTotalLeaves()
        updatedAvailableLeaves == employeeResponse.getAvailableLeaves()
    }

    def "updateEmployee should throw ResourceNotFoundException if employee id is not found"() {
        given:
        Long id = 5L
        UpdateEmployeeRequest updateEmployeeRequest = Mock()

        employeeService.updateEmployee(id, updateEmployeeRequest) >> { throw new ResourceNotFoundException() }

        when:
        employeeController.updateEmployee(id, updateEmployeeRequest)

        then:
        thrown(ResourceNotFoundException)
    }

    def "updateEmployee should throw an InvalidOperationException when EmployeeService updateEmployee throws EmployeeModificationNotAllowedException"() {
        given:
        Long id = 1L
        UpdateEmployeeRequest updateEmployeeRequest = Mock(UpdateEmployeeRequest)
        String errorCode = "MODIFICATION_NOT_ALLOWED"

        employeeService.updateEmployee(id, updateEmployeeRequest) >> { throw new EmployeeModificationNotAllowedException() }

        when:
        employeeController.updateEmployee(id, updateEmployeeRequest)

        then:
        def e = thrown(InvalidOperationException)
        errorCode == e.getErrorCode()
    }

    def "updateEmployee should throw InvalidOperationException when EmployeeService updateEmployee throws LeaveCountModificationException"() {
        given:
        Long id = 1L
        UpdateEmployeeRequest updateEmployeeRequest = Mock(UpdateEmployeeRequest)
        String errorCode = "INVALID_LEAVE_MODIFICATION"


        employeeService.updateEmployee(id, updateEmployeeRequest) >> { throw new LeaveCountModificationException() }

        when:
        employeeController.updateEmployee(id, updateEmployeeRequest)

        then:
        def e = thrown(InvalidOperationException)
        errorCode == e.getErrorCode()
    }
}