package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication

import com.synacy.graduate.program.leaveapp.leave_management.employee.Employee
import com.synacy.graduate.program.leaveapp.leave_management.web.PageResponse
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.InvalidOperationException
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.InvalidRequestException
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.ResourceNotFoundException
import org.springframework.cglib.core.Local
import org.springframework.data.domain.Page
import spock.lang.Specification

import java.time.LocalDate

class LeaveApplicationControllerSpec extends Specification {

    LeaveApplicationController leaveApplicationController
    LeaveApplicationService leaveApplicationService = Mock()

    def setup() {
        leaveApplicationController = new LeaveApplicationController(leaveApplicationService)
    }

    def "getLeaveApplications should return a paginated list of all leave applications"() {
        given:
        int max = 10;
        int page = 1;
        int totalCount = 2

        Long id1 = 1
        Long employeeId1 = 2
        String employeeFirstName1 = "John"
        String employeeLastName1 = "Doe"
        String employeeName1 = "John Doe"
        Long managerId1 = 1
        String managerFirstName1 = "Manager"
        String managerLastName1 = "Doe"
        String managerName1 = "Manager Doe"
        LocalDate startDate1 = LocalDate.now()
        LocalDate endDate1 = LocalDate.now()
        int workDays1 = 1
        String reason1 = "Reason"
        LeaveApplicationStatus status1 = LeaveApplicationStatus.PENDING

        Long id2 = 2
        Long employeeId2 = 3
        String employeeFirstName2 = "John"
        String employeeLastName2 = "Deer"
        String employeeName2 = "John Deer"
        Long managerId2 = 2
        String managerFirstName2 = "Manager"
        String managerLastName2 = "Deer"
        String managerName2 = "Manager Deer"
        LocalDate startDate2 = LocalDate.now()
        LocalDate endDate2 = LocalDate.now()
        int workDays2 = 1
        String reason2 = "Reason"
        LeaveApplicationStatus status2 = LeaveApplicationStatus.APPROVED

        Employee employee1 = Mock() {
            id >> employeeId1
            firstName >> employeeFirstName1
            lastName >> employeeLastName1
        }
        Employee employee2 = Mock() {
            id >> employeeId2
            firstName >> employeeFirstName2
            lastName >> employeeLastName2
        }
        Employee manager1 = Mock() {
            id >> managerId1
            firstName >> managerFirstName1
            lastName >> managerLastName1
        }
        Employee manager2 = Mock() {
            id >> managerId2
            firstName >> managerFirstName2
            lastName >> managerLastName2
        }

        LeaveApplication leave1 = Mock() {
            id >> id1
            employee >> employee1
            manager >> manager1
            startDate >> startDate1
            endDate >> endDate1
            workDays >> workDays1
            reason >> reason1
            status >> status1
        }
        LeaveApplication leave2 = Mock() {
            id >> id2
            employee >> employee2
            manager >> manager2
            startDate >> startDate2
            endDate >> endDate2
            workDays >> workDays2
            reason >> reason2
            status >> status2
        }

        List<LeaveApplication> leaveList = [leave1, leave2]

        Page<LeaveApplication> paginatedLeaves = Mock() {
            content >> leaveList
            totalElements >> totalCount
        }

        when:
        PageResponse<ManagerialLeaveApplicationResponse> response = leaveApplicationController.getLeaveApplications(max, page)

        then:
        1 * leaveApplicationService.getAllLeaveApplications(max, page) >> paginatedLeaves
        totalCount == response.totalCount()
        page == response.pageNumber()

        id1 == response.content()[0].getId()
        employeeName1 == response.content()[0].getEmployeeName()
        managerName1 == response.content()[0].getManagerName()
        startDate1 == response.content()[0].getStartDate()
        endDate1 == response.content()[0].getEndDate()
        workDays1 == response.content()[0].getWorkDays()
        reason1 == response.content()[0].getReason()
        status1 == response.content()[0].getStatus()

        id2 == response.content()[1].getId()
        employeeName2 == response.content()[1].getEmployeeName()
        managerName2 == response.content()[1].getManagerName()
        startDate2 == response.content()[1].getStartDate()
        endDate2 == response.content()[1].getEndDate()
        workDays2 == response.content()[1].getWorkDays()
        reason2 == response.content()[1].getReason()
        status2 == response.content()[1].getStatus()
    }

    def "getLeavesByManager should throw an InvalidOperationException when the role of the provided ID is not MANAGER"() {
        given:
        int page = 1
        int max = 10
        Long filterId = 1

        String errorCode = "NOT_A_MANAGER"

        leaveApplicationService.getLeavesByManager(max, page, filterId) >> { throw new NotAManagerException() }

        when:
        leaveApplicationController.getLeavesByManager(max, page, filterId)

        then:
        InvalidOperationException e = thrown(InvalidOperationException)
        errorCode == e.errorCode
    }

    def "getLeavesByManager should throw an InvalidRequestException when no employee is associated with the given ID"() {
        given:
        int max = 10
        int page = 1
        Long filterId = 1

        leaveApplicationService.getLeavesByManager(max, page, filterId) >> { throw new ResourceNotFoundException() }

        when:
        leaveApplicationController.getLeavesByManager(max, page, filterId)

        then:
        thrown(InvalidRequestException)
    }

    def "getLeavesByManager should return a paginated leave applications of all employees under the direct supervision of the given manager"() {
        given:
        int max = 10
        int page = 1
        Long managerIdFilter = 1
        int totalCount = 2

        Long managerId = 1
        String managerFirstName = "Man"
        String managerLastName = "Doe"

        Long id1 = 1
        String employeeFirstName1 = "John"
        String employeeLastName1 = "Doe"
        String employeeName1 = "John Doe"
        LocalDate startDate1 = LocalDate.now()
        LocalDate endDate1 = LocalDate.now()
        int workDays1 = 1
        String reason1 = "Reason 1"
        LeaveApplicationStatus status1 = LeaveApplicationStatus.APPROVED

        Long id2 = 2
        String employeeFirstName2 = "Johny"
        String employeeLastName2 = "Doey"
        String employeeName2 = "Johny Doey"
        LocalDate startDate2 = LocalDate.now()
        LocalDate endDate2 = LocalDate.now()
        int workDays2 = 1
        String reason2 = "Reason 1"
        LeaveApplicationStatus status2 = LeaveApplicationStatus.APPROVED

        Employee employee1 = Mock(Employee) {
            firstName >> employeeFirstName1
            lastName >> employeeLastName1
        }
        Employee employee2 = Mock(Employee) {
            firstName >> employeeFirstName2
            lastName >> employeeLastName2
        }
        Employee managerObject = Mock(Employee) {
            id >> managerId
            firstName >> managerFirstName
            lastName >> managerLastName
        }

        LeaveApplication leaveApplication1 = Mock() {
            id >> id1
            employee >> employee1
            manager >> managerObject
            startDate >> startDate1
            endDate >> endDate1
            workDays >> workDays1
            reason >> reason1
            status >> status1
        }

        LeaveApplication leaveApplication2 = Mock() {
            id >> id2
            employee >> employee2
            manager >> managerObject
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

        when:
        PageResponse<ManagerialLeaveApplicationResponse> response = leaveApplicationController.getLeavesByManager(max, page, managerIdFilter)

        then:
        1 * leaveApplicationService.getLeavesByManager(max, page, managerIdFilter) >> leaveApplicationPage
        totalCount == response.totalCount()
        page == response.pageNumber()

        employeeName1 == response.content()[0].getEmployeeName()
        startDate1 == response.content()[0].getStartDate()
        endDate1 == response.content()[0].getEndDate()
        workDays1 == response.content()[0].getWorkDays()
        reason1 == response.content()[0].getReason()
        status1 == response.content()[0].getStatus()

        employeeName2 == response.content()[1].getEmployeeName()
        startDate2 == response.content()[1].getStartDate()
        endDate2 == response.content()[1].getEndDate()
        workDays2 == response.content()[1].getWorkDays()
        reason2 == response.content()[1].getReason()
        status2 == response.content()[1].getStatus()
    }

    def "getLeaveByEmployee should throw an InvalidRequestException when no employee is associated with the given ID"() {
        given:
        int max = 10
        int page = 1
        int employeeId = 1

        leaveApplicationService.getLeavesByEmployee(max, page, employeeId) >> { throw new ResourceNotFoundException() }

        when:
        leaveApplicationController.getLeaveByEmployee(max, page, employeeId)

        then:
        thrown(InvalidRequestException)
    }

    def "getLeaveByEmployee should return a paginated list of leaves by the employee"() {
        given:
        int max = 10
        int page = 1
        int totalCount = 2

        int employeeId = 1
        String employeeFirstName = "John"
        String employeeLastName = "Doe"
        String employeeName = "John Doe"

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
        LocalDate startDate2 = LocalDate.now()
        LocalDate endDate2 = LocalDate.now()
        int workDays2 = 1
        String reason2 = "Reason 1"
        LeaveApplicationStatus status2 = LeaveApplicationStatus.APPROVED

        Employee employeeObj = Mock(Employee) {
            id >> employeeId
            firstName >> employeeFirstName
            lastName >> employeeLastName
        }
        Employee managerObject = Mock(Employee) {
            id >> managerId
            firstName >> managerFirstName
            lastName >> managerLastName
        }

        LeaveApplication leaveApplication1 = Mock() {
            id >> id1
            employee >> employeeObj
            manager >> managerObject
            startDate >> startDate1
            endDate >> endDate1
            workDays >> workDays1
            reason >> reason1
            status >> status1
        }

        LeaveApplication leaveApplication2 = Mock() {
            id >> id2
            employee >> employeeObj
            manager >> managerObject
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

        when:
        PageResponse<EmployeeLeaveApplicationResponse> response = leaveApplicationController.getLeaveByEmployee(max, page, employeeId)

        then:
        1 * leaveApplicationService.getLeavesByEmployee(max, page, employeeId) >> leaveApplicationPage
        totalCount == response.totalCount()
        page == response.pageNumber()

        startDate1 == response.content()[0].getStartDate()
        endDate1 == response.content()[0].getEndDate()
        workDays1 == response.content()[0].getWorkDays()
        reason1 == response.content()[0].getReason()
        status1 == response.content()[0].getStatus()

        startDate2 == response.content()[1].getStartDate()
        endDate2 == response.content()[1].getEndDate()
        workDays2 == response.content()[1].getWorkDays()
        reason2 == response.content()[1].getReason()
        status2 == response.content()[1].getStatus()
    }

    def "createLeaveApplication should call LeaveApplicationService createLeaveApplication and return an EmployeeLeaveApplicationResponse with correct properties"() {
        given:
        Long leaveId = 1L
        LocalDate startDate = LocalDate.now()
        LocalDate endDate = startDate
        Integer workDays = 1
        String reason = "Reason for applying for a leave"
        LeaveApplicationStatus status = LeaveApplicationStatus.PENDING

        CreateLeaveApplicationRequest leaveRequest = Mock()

        LeaveApplication leaveApplication = Mock() {
            getId() >> leaveId
            getStartDate() >> startDate
            getEndDate() >> endDate
            getWorkDays() >> workDays
            getReason() >> reason
            getStatus() >> status
        }

        when:
        EmployeeLeaveApplicationResponse employeeLeaveApplicationResponse = leaveApplicationController.createLeaveApplication(leaveRequest)

        then:
        1 * leaveApplicationService.createLeaveApplication(leaveRequest) >> leaveApplication
        leaveId == employeeLeaveApplicationResponse.getId()
        startDate == employeeLeaveApplicationResponse.getStartDate()
        endDate == employeeLeaveApplicationResponse.getEndDate()
        workDays == employeeLeaveApplicationResponse.getWorkDays()
        reason == employeeLeaveApplicationResponse.getReason()
        status == employeeLeaveApplicationResponse.getStatus()
    }

    def "createLeaveApplication should throw InvalidOperationException with expected errorCode and errorMessage if #outputDescription"() {
        given:
        String expectedErrorCode = "INVALID_LEAVE_DATES"
        CreateLeaveApplicationRequest leaveRequest = Mock() {
            getStartDate() >> startDate
            getEndDate() >> endDate
        }

        leaveApplicationService.createLeaveApplication(leaveRequest) >> { throw new InvalidLeaveDateException(expectedErrorMessage) }

        when:
        leaveApplicationController.createLeaveApplication(leaveRequest)

        then:
        def exception = thrown(InvalidOperationException)
        expectedErrorCode == exception.getErrorCode()
        expectedErrorMessage == exception.getErrorMessage()

        where:
                  startDate          |         endDate         |                expectedErrorMessage                |   outputDescription
                    null             |           null          |      "Start date or end date cannot be null."      | "startDate or endDate is null"
        LocalDate.now().plusDays(2)  |      LocalDate.now()    |        "Start date cannot be after end date."      | "startDate is set after endDate"
        LocalDate.now().minusDays(1) |  startDate.plusDays(1)  |     "Start date cannot be before current date."    | "startDate is set before the current date"
               LocalDate.now()       |  startDate.plusDays(1)  |         "Overlapping leave applications."          | "employee has an existing leave application that overlaps with current request"
    }

    def "createLeaveApplication should throw InvalidRequestException if employee does not exist"() {
        given:
        Long id = 1L

        CreateLeaveApplicationRequest leaveRequest = Mock()
        leaveRequest.getEmployeeId() >> id

        leaveApplicationService.createLeaveApplication(leaveRequest) >> { throw new ResourceNotFoundException() }

        when:
        leaveApplicationController.createLeaveApplication(leaveRequest)

        then:
        thrown(InvalidRequestException)
    }

    def "createLeaveApplication should throw InvalidOperationException with expected errorCode and errorMessage if employee does not have enough available leaves"() {
        given:
        String expectedErrorCode = "INSUFFICIENT_LEAVE_CREDITS"
        String expectedErrorMessage = "Employee has insufficient leave credits"
        LocalDate startDate = LocalDate.now().plusDays(3)

        CreateLeaveApplicationRequest leaveRequest = Mock() {
            getStartDate() >> startDate
            getStartDate() >> startDate.plusDays(5)
        }

        leaveApplicationService.createLeaveApplication(leaveRequest) >> { throw new InvalidLeaveApplicationException(expectedErrorMessage) }

        when:
        leaveApplicationController.createLeaveApplication(leaveRequest)

        then:
        def exception = thrown(InvalidOperationException)
        expectedErrorCode == exception.getErrorCode()
        expectedErrorMessage == exception.getErrorMessage()
    }

    def "updateLeaveApplication should throw a ResourceNotFoundException when leave application does not exist"() {
        given:
        Long id = 1
        UpdateLeaveApplicationRequest updateLeaveRequest = Mock(UpdateLeaveApplicationRequest)

        leaveApplicationService.getLeaveApplicationById(id) >> Optional.empty()

        when:
        leaveApplicationController.updateLeaveApplication(id, updateLeaveRequest)

        then:
        thrown(ResourceNotFoundException)
    }

    def "updateLeaveApplication should update and return the leave application request when leave application with given id exists and request status is #leaveStatus"() {
        given:
        Long leaveId = 1
        LeaveApplicationStatus expectedStatus = leaveStatus
        LeaveApplication existingLeave = Mock(LeaveApplication) {
            id >> leaveId
            status >> LeaveApplicationStatus.PENDING
        }

        UpdateLeaveApplicationRequest request = Mock(UpdateLeaveApplicationRequest) {
            status >> expectedStatus
        }

        LeaveApplication updatedLeave = Mock(LeaveApplication) {
            id >> leaveId
            status >> expectedStatus
        }

        leaveApplicationService.getLeaveApplicationById(leaveId) >> Optional.of(existingLeave)

        when:
        EmployeeLeaveApplicationResponse result = leaveApplicationController.updateLeaveApplication(leaveId, request)

        then:
        1 * leaveApplicationService.updateLeaveApplication(existingLeave, request) >> updatedLeave
        expectedStatus == result.status

        where:
        leaveStatus << [LeaveApplicationStatus.APPROVED, LeaveApplicationStatus.REJECTED]
    }

    def "updateLeaveApplication should throw an InvalidOperationException when the status of leave application to update is not PENDING"() {
        given:
        Long leaveId = 1
        LeaveApplication existingLeave = Mock(LeaveApplication) {
            id >> leaveId
            status >> LeaveApplicationStatus.APPROVED
        }

        UpdateLeaveApplicationRequest request = Mock(UpdateLeaveApplicationRequest)

        leaveApplicationService.getLeaveApplicationById(leaveId) >> Optional.of(existingLeave)
        leaveApplicationService.updateLeaveApplication(existingLeave, request) >>
                { throw new StatusNotPendingException("Leave application status is not PENDING.") }

        when:
        leaveApplicationController.updateLeaveApplication(leaveId, request)

        then:
        thrown(InvalidOperationException)
    }

    def "updateLeaveApplication should throw an InvalidOperationException when the leave request status is CANCELLED"() {
        given:
        Long leaveId = 1
        LeaveApplication existingLeave = Mock(LeaveApplication) {
            id >> leaveId
            status >> LeaveApplicationStatus.PENDING
        }
        UpdateLeaveApplicationRequest request = Mock(UpdateLeaveApplicationRequest) {
            status >> LeaveApplicationStatus.CANCELLED
        }

        leaveApplicationService.getLeaveApplicationById(leaveId) >> Optional.of(existingLeave)
        leaveApplicationService.updateLeaveApplication(existingLeave, request) >>
                { throw new InvalidLeaveApplicationException("Cancellation requests are not allowed in this method") }

        when:
        leaveApplicationController.updateLeaveApplication(leaveId, request)

        then:
        thrown(InvalidOperationException)
    }

    def "cancelLeaveApplication should throw a ResourceNotFoundException when leave application of given id does not exist"() {
        given:
        Long leaveId = 1
        LeaveApplication leave = Mock(LeaveApplication) {
            id >> leaveId
        }

        leaveApplicationService.getLeaveApplicationById(leaveId) >> Optional.empty()

        when:
        leaveApplicationController.cancelLeaveApplication(leaveId)

        then:
        thrown(ResourceNotFoundException)
    }

    def "cancelLeaveApplication should cancel the leave application when leave application exists"() {
        given:
        Long leaveId = 1
        LeaveApplication leave = Mock(LeaveApplication) {
            id >> leaveId
        }

        leaveApplicationService.getLeaveApplicationById(leaveId) >> Optional.of(leave)

        when:
        leaveApplicationController.cancelLeaveApplication(leaveId)

        then:
        1 * leaveApplicationService.cancelLeaveApplication(leave)
    }

    def "cancelLeaveApplication should throw an InvalidOperationException when the leave status to cancel is not PENDING"() {
        given:
        Long leaveId = 1
        LeaveApplication leave = Mock(LeaveApplication) {
            id >> leaveId
        }

        leaveApplicationService.getLeaveApplicationById(leaveId) >> Optional.of(leave)
        leaveApplicationService.cancelLeaveApplication(leave) >>
                { throw new StatusNotPendingException("Leave application status is not PENDING.") }

        when:
        leaveApplicationController.cancelLeaveApplication(leaveId)

        then:
        thrown(InvalidOperationException)
    }
}