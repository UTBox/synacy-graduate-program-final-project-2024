package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication

import com.synacy.graduate.program.leaveapp.leave_management.employee.Employee
import com.synacy.graduate.program.leaveapp.leave_management.employee.EmployeeRole
import com.synacy.graduate.program.leaveapp.leave_management.employee.EmployeeService
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.ResourceNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import spock.lang.Specification

import java.time.LocalDate


class LeaveApplicationServiceSpec extends Specification {
    LeaveApplicationService leaveApplicationService
    LeaveApplicationRepository leaveApplicationRepository = Mock()
    EmployeeService employeeService = Mock()
    LeaveQuantityModifier leaveQuantityModifier = Mock()

    def setup() {
        leaveApplicationService = new LeaveApplicationService(leaveApplicationRepository, employeeService, leaveQuantityModifier)
    }

    def "getLeaveApplicationsByStatus should return a paginated leaves with a #requestStatus status"(){
        given:
        int requestPage = 1
        int subtractedPage = 0
        int max = 5
        Long expectedTotalElements = 2
        int expectedTotalPages = 1

        LeaveApplication leave1 = Mock(LeaveApplication) {
            status >> requestStatus
        }
        LeaveApplication leave2 = Mock(LeaveApplication) {
            status >> requestStatus
        }

        List<LeaveApplication> leaveApplicationList = [leave1, leave2]
        Page<LeaveApplication> paginatedLeaves = Mock(Page){
            content >> leaveApplicationList
            totalElements >> expectedTotalElements
            totalPages >> expectedTotalPages
        }

        Pageable pageable = PageRequest.of(subtractedPage, max, Sort.by("id"));

        when:
        Page<LeaveApplication> response =  leaveApplicationService.getLeaveApplicationsByStatus(max, requestPage, requestStatus)

        then:
        1 * leaveApplicationRepository.findAllByStatus(requestStatus, pageable) >> paginatedLeaves

        expectedTotalElements == response.getTotalElements()
        expectedTotalPages == response.getTotalPages()
        leaveApplicationList == response.getContent()

        where:
        requestStatus << [LeaveApplicationStatus.PENDING, LeaveApplicationStatus.APPROVED, LeaveApplicationStatus.REJECTED, LeaveApplicationStatus.CANCELLED]
    }

    def "getLeavesByManagerAndStatus should return a paginated leaves with a #requestStatus status and with the given manager Id"(){
        given:
        int requestPage = 1
        int subtractedPage = 0
        int max = 5
        Long expectedTotalElements = 2
        int expectedTotalPages = 1

        Long requestedManagerId = 1
        Employee requestedManager = Mock(Employee) {
            id >> requestedManagerId
            role >> EmployeeRole.MANAGER
        }

        LeaveApplication leave1 = Mock(LeaveApplication) {
            status >> requestStatus
            manager >> requestedManager
        }
        LeaveApplication leave2 = Mock(LeaveApplication) {
            status >> requestStatus
            manager >> requestedManager
        }

        List<LeaveApplication> leaveApplicationList = [leave1, leave2]
        Page<LeaveApplication> paginatedLeaves = Mock(Page){
            content >> leaveApplicationList
            totalElements >> expectedTotalElements
            totalPages >> expectedTotalPages
        }

        Pageable pageable = PageRequest.of(subtractedPage, max, Sort.by("id"));
        employeeService.getEmployeeById(requestedManagerId) >> Optional.of(requestedManager)

        when:
        Page<LeaveApplication> response =  leaveApplicationService.getLeavesByManagerAndStatus(max, requestPage, requestedManagerId, requestStatus)

        then:
        1 * leaveApplicationRepository.findAllByManagerAndStatus(requestedManager, requestStatus, pageable) >> paginatedLeaves

        expectedTotalElements == response.getTotalElements()
        expectedTotalPages == response.getTotalPages()
        leaveApplicationList == response.getContent()

        where:
        requestStatus << [LeaveApplicationStatus.PENDING, LeaveApplicationStatus.APPROVED, LeaveApplicationStatus.REJECTED, LeaveApplicationStatus.CANCELLED]
    }

    def "getLeavesByManagerAndStatus should throw a NotAManagerException when the employee associated with the given ID is not a manager"(){
        given:
        int page = 1
        int max = 5

        LeaveApplicationStatus requestStatus = LeaveApplicationStatus.PENDING

        Long requestedManagerId = 1
        Employee requestedManager = Mock(Employee) {
            id >> requestedManagerId
            role >> requestRole
        }

        LeaveApplication leave1 = Mock(LeaveApplication) {
            status >> requestStatus
            manager >> requestedManager
        }
        LeaveApplication leave2 = Mock(LeaveApplication) {
            status >> requestStatus
            manager >> requestedManager
        }

        employeeService.getEmployeeById(requestedManagerId) >> Optional.of(requestedManager)

        when:
        leaveApplicationService.getLeavesByManagerAndStatus(max, page, requestedManagerId, requestStatus)

        then:
        thrown(NotAManagerException)

        where:
        requestRole << [EmployeeRole.EMPLOYEE, EmployeeRole.HR_ADMIN]
    }

    def "getLeavesByManagerAndStatus should throw a ResourceNotFoundException when no employee is associated with the given ID"(){
        given:
        int page = 1
        int max = 5
        int id = 1

        employeeService.getEmployeeById(id) >> Optional.empty()

        when:
        leaveApplicationService.getLeavesByManagerAndStatus(max, page, id, LeaveApplicationStatus.PENDING)

        then:
        thrown(ResourceNotFoundException)
    }

    def "getLeavesByEmployee should return a paginated list of leaves by the employee"() {
        given:
        int passedPage = 1
        int subtractedPage = 0
        int max = 10
        int totalCount = 2

        Long employeeId = 1
        String employeeFirstName = "John"
        String employeeLastName = "Doe"

        Long managerId = 1
        String managerFirstName = "Man"
        String managerLastName = "Doe"

        Long id1 = 1
        LocalDate startDate1 = LocalDate.now()
        LocalDate endDate1 = LocalDate.now()
        int workDays1 = 1
        String reason1 = "Reason 1"
        LeaveApplicationStatus status1 = LeaveApplicationStatus.APPROVED

        Long id2 = 2
        LocalDate startDate2 = LocalDate.now().plusDays(1)
        LocalDate endDate2 = LocalDate.now().plusDays(3)
        int workDays2 = 2
        String reason2 = "Reason 2"
        LeaveApplicationStatus status2 = LeaveApplicationStatus.APPROVED

        Employee employeeObj = Mock(Employee) {
            id >> employeeId
            firstName >> employeeFirstName
            lastName >> employeeLastName
        }

        Employee managerObj = Mock(Employee) {
            id >> managerId
            firstName >> managerFirstName
            lastName >> managerLastName
            role >> EmployeeRole.MANAGER
        }

        LeaveApplication leaveApplication1 = Mock() {
            id >> id1
            employee >> employeeObj
            manager >> managerObj
            startDate >> startDate1
            endDate >> endDate1
            workDays >> workDays1
            reason >> reason1
            status >> status1
        }

        LeaveApplication leaveApplication2 = Mock() {
            id >> id2
            employee >> employeeObj
            manager >> managerObj
            startDate >> startDate2
            endDate >> endDate2
            workDays >> workDays2
            reason >> reason2
            status >> status2
        }

        List<LeaveApplication> leaveApplicationList = [leaveApplication1, leaveApplication2]
        Page<LeaveApplication> leaveApplicationPage = Mock() {
            content >> leaveApplicationList
            totalElements >> totalCount
        }

        Pageable pageable = PageRequest.of(subtractedPage, max, Sort.by("id"));

        when:
        Page<LeaveApplication> response = leaveApplicationService.getLeavesByEmployee(max, passedPage, employeeId)

        then:
        1 * employeeService.getEmployeeById(employeeId) >> Optional.of(employeeObj)
        1 * leaveApplicationRepository.findAllByEmployee(employeeObj, pageable) >> leaveApplicationPage

        totalCount == response.getTotalElements()
        leaveApplicationList == response.getContent()


    }

    def "getLeavesByEmployee should throw a ResourceNotFoundException when no employee is associated with the given ID"() {
        given:
        int page = 1
        int max = 10
        Long employeeId = 1

        employeeService.getEmployeeById(employeeId) >> Optional.empty()

        when:
        leaveApplicationService.getLeavesByEmployee(max, page, employeeId)

        then:
        thrown(ResourceNotFoundException)
    }

    def "getLeaveApplicationById should return a leave application with the given existing id"() {
        given:
        Long leaveId = 1
        LeaveApplication leave = Mock(LeaveApplication) {
            id >> leaveId
        }

        when:
        Optional<LeaveApplication> result = leaveApplicationService.getLeaveApplicationById(1L)

        then:
        1 * leaveApplicationRepository.findById(_) >> Optional.of(leave)
        leaveId == result.get().id
    }

    def "createLeaveApplication should create a LeaveApplication and save it to the repository with the expected properties"() {
        given:
        LocalDate startDate = LocalDate.now()
        LocalDate endDate = startDate
        Integer leaveWorkDays = 1
        String reason = "Reason for leave request"

        CreateLeaveApplicationRequest leaveRequest = Mock() {
            getEmployeeId() >> 1L
            getStartDate() >> startDate
            getEndDate() >> endDate
            getReason() >> reason
        }

        Employee manager = Mock()

        Employee employee = Mock() {
            getManager() >> manager
        }

        LeaveApplication leaveApplication = Mock() {
            getEmployee() >> employee
            getManager() >> manager
            getStartDate() >> leaveRequest.getStartDate()
            getEndDate() >> leaveRequest.getEndDate()
            getWorkDays() >> leaveWorkDays
            getReason() >> leaveRequest.getReason()
            getStatus() >> LeaveApplicationStatus.PENDING
        }

        employeeService.getEmployeeById(leaveRequest.getEmployeeId()) >> Optional.of(employee)

        leaveApplicationRepository.countOverlappingLeaveApplications(
                employee.getId(),
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate()) >> 0

        when:
        leaveApplicationService.createLeaveApplication(leaveRequest)

        then:
        1 * leaveApplicationRepository.save(_ as LeaveApplication) >> { LeaveApplication actualApplication ->
            assert leaveApplication.getEmployee() == actualApplication.getEmployee()
            assert leaveApplication.getManager() == actualApplication.getManager()
            assert leaveApplication.getStartDate() == actualApplication.getStartDate()
            assert leaveApplication.getEndDate() == actualApplication.getEndDate()
            assert leaveApplication.getWorkDays() == actualApplication.getWorkDays()
            assert leaveApplication.getReason() == actualApplication.getReason()
            assert leaveApplication.getStatus() == actualApplication.getStatus()
        }
    }

    def "createLeaveApplication should throw ResourceNotFoundException if employee does not exist"() {
        given:
        CreateLeaveApplicationRequest leaveRequest = Mock()
        leaveRequest.getEmployeeId() >> 1L

        employeeService.getEmployeeById(leaveRequest.getEmployeeId()) >> Optional.empty()

        when:
        leaveApplicationService.createLeaveApplication(leaveRequest)

        then:
        thrown(ResourceNotFoundException)
    }

    def "createLeaveApplication should throw InvalidLeaveDateException when #outputDescription"() {
        given:
        Long employeeId = 1L
        Integer overlapLeaveCount = 1

        CreateLeaveApplicationRequest leaveRequest = Mock() {
            getEmployeeId() >> employeeId
            getStartDate() >> startDate
            getEndDate() >> endDate
        }

        Employee employee = Mock() {
            getId() >> employeeId
        }

        employeeService.getEmployeeById(leaveRequest.getEmployeeId()) >> Optional.of(employee)
        leaveApplicationRepository.countOverlappingLeaveApplications(employeeId, startDate, endDate) >> overlapLeaveCount

        when:
        leaveApplicationService.createLeaveApplication(leaveRequest)

        then:
        def exception = thrown(InvalidLeaveDateException)
        expectedErrorMessage == exception.getMessage()

        where:
                 startDate           |         endDate         |                expectedErrorMessage                |   outputDescription
                   null              |           null          |      "Start date or end date cannot be null."      | "startDate or endDate is null"
        LocalDate.now().plusDays(2)  |      LocalDate.now()    |        "Start date cannot be after end date."      | "startDate is set after endDate"
        LocalDate.now().minusDays(1) |  startDate.plusDays(1)  |     "Start date cannot be before current date."    | "startDate is set before the current date"
              LocalDate.now()        |  startDate.plusDays(1)  |         "Overlapping leave applications."          | "employee has an existing leave application that overlaps with current request"
    }

    def "updateLeaveApplication should throw an StatusNotPendingException when status of leave application to update is not PENDING"() {
        given:
        LeaveApplication leave = Mock(LeaveApplication) {
            status >> leaveStatus
        }
        UpdateLeaveApplicationRequest request = Mock(UpdateLeaveApplicationRequest)

        when:
        leaveApplicationService.updateLeaveApplication(leave, request)

        then:
        thrown(StatusNotPendingException)

        where:
        leaveStatus << [LeaveApplicationStatus.APPROVED, LeaveApplicationStatus.REJECTED, LeaveApplicationStatus.CANCELLED]
    }

    def "updateLeaveApplication should not modify leave quantity but save the leave application when leave status is PENDING and request status is APPROVED"() {
        given:
        LeaveApplication leave = new LeaveApplication()
        leave.status = LeaveApplicationStatus.PENDING

        UpdateLeaveApplicationRequest request = Mock(UpdateLeaveApplicationRequest) {
            status >> LeaveApplicationStatus.APPROVED
        }

        when:
        leaveApplicationService.updateLeaveApplication(leave, request)

        then:
        0 * leaveQuantityModifier.addLeaveQuantityBasedOnRejectedOrCancelledRequest(leave)
        1 * leaveApplicationRepository.save(_) >> { LeaveApplication savedLeave ->
            assert LeaveApplicationStatus.APPROVED == savedLeave.status
        }
    }

    def "updateLeaveApplication should modify leave quantity and save the leave application when leave status is PENDING and request status is REJECTED"() {
        given:
        LeaveApplication leave = new LeaveApplication()
        leave.status = LeaveApplicationStatus.PENDING

        UpdateLeaveApplicationRequest request = Mock(UpdateLeaveApplicationRequest) {
            status >> LeaveApplicationStatus.REJECTED
        }

        when:
        leaveApplicationService.updateLeaveApplication(leave, request)

        then:
        1 * leaveQuantityModifier.addLeaveQuantityBasedOnRejectedOrCancelledRequest(leave)
        1 * leaveApplicationRepository.save(_) >> { LeaveApplication savedLeave ->
            assert LeaveApplicationStatus.REJECTED == savedLeave.status
        }
    }

    def "updateLeaveApplication should throw an InvalidLeaveApplicationException when leave request status is CANCELLED"() {
        given:
        LeaveApplication leave = Mock(LeaveApplication) {
            status >> LeaveApplicationStatus.PENDING
        }

        UpdateLeaveApplicationRequest request = Mock(UpdateLeaveApplicationRequest) {
            status >> LeaveApplicationStatus.CANCELLED
        }

        when:
        leaveApplicationService.updateLeaveApplication(leave, request)

        then:
        thrown(InvalidLeaveApplicationException)
    }

    def "cancelLeaveApplication should throw a StatusNotPendingException when status of leave application to cancel is not PENDING"() {
        given:
        LeaveApplication leave = Mock(LeaveApplication) {
            status >> leaveStatus
        }

        when:
        leaveApplicationService.cancelLeaveApplication(leave)

        then:
        thrown(StatusNotPendingException)

        where:
        leaveStatus << [LeaveApplicationStatus.APPROVED, LeaveApplicationStatus.REJECTED, LeaveApplicationStatus.CANCELLED]
    }

    def "cancelLeaveApplication should cancel and save the leave application when leave application status is PENDING"() {
        given:
        LeaveApplication leave = new LeaveApplication()
        leave.status = LeaveApplicationStatus.PENDING

        when:
        leaveApplicationService.cancelLeaveApplication(leave)

        then:
        1 * leaveQuantityModifier.addLeaveQuantityBasedOnRejectedOrCancelledRequest(leave)
        1 * leaveApplicationRepository.save(leave) >> { LeaveApplication savedLeave ->
            assert LeaveApplicationStatus.CANCELLED == savedLeave.status
        }
    }

    def "setLeaveApplication should return a LeaveApplication with the expected properties"() {
        given:
        LocalDate startDate = LocalDate.now()
        LocalDate endDate = startDate
        Integer leaveWorkDays = 1
        String reason = "Reason for leave request"
        LeaveApplicationStatus status = LeaveApplicationStatus.PENDING

        Employee manager = Mock()

        Employee employee = Mock() {
            getManager() >> manager
        }

        CreateLeaveApplicationRequest leaveRequest = Mock() {
            getStartDate() >> startDate
            getEndDate() >> endDate
            getReason() >> reason
        }

        when:
        LeaveApplication actualApplication = LeaveApplicationService.setLeaveApplication(employee, leaveRequest, leaveWorkDays)

        then:
        employee == actualApplication.getEmployee()
        manager == actualApplication.getManager()
        startDate == actualApplication.getStartDate()
        endDate == actualApplication.getEndDate()
        leaveWorkDays == actualApplication.getWorkDays()
        reason == actualApplication.getReason()
        status == actualApplication.getStatus()
    }
}