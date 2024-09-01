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

    def "getAllLeaveApplications should return a paginated list of all leave applications"() {
        given:
        int max = 10;
        int passedPage = 1;
        int subtractedPage = 0;
        int totalCount = 2

        Long id1 = 1
        Long employeeId1 = 2
        Long managerId1 = 1
        LocalDate startDate1 = LocalDate.now()
        LocalDate endDate1 = LocalDate.now()
        int workDays1 = 1
        String reason1 = "Reason"
        LeaveApplicationStatus status1 = LeaveApplicationStatus.PENDING

        Long id2 = 2
        Long employeeId2 = 3
        Long managerId2 = 2
        LocalDate startDate2 = LocalDate.now()
        LocalDate endDate2 = LocalDate.now()
        int workDays2 = 1
        String reason2 = "Reason"
        LeaveApplicationStatus status2 = LeaveApplicationStatus.APPROVED

        Employee employee1 = Mock() {
            id >> employeeId1
        }
        Employee employee2 = Mock() {
            id >> employeeId2
        }
        Employee manager1 = Mock() {
            id >> managerId1
        }
        Employee manager2 = Mock() {
            id >> managerId2
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

        Pageable pageable = PageRequest.of(subtractedPage, max, Sort.by("id"));

        when:
        Page<LeaveApplication> response = leaveApplicationService.getAllLeaveApplications(max, passedPage)

        then:
        1 * leaveApplicationRepository.findAll(pageable) >> paginatedLeaves
        totalCount == response.getTotalElements()
        leaveList == response.getContent()
    }

    def "getLeavesByManager should throw a NotAManagerException when the provided ID has a #employeeRole role"() {
        given:
        int max = 10
        int page = 1
        Long filterId = 1
        Employee notManager = Mock(Employee) {
            id >> filterId
            role >> employeeRole
        }

        employeeService.getEmployeeById(filterId) >> Optional.of(notManager)

        when:
        leaveApplicationService.getLeavesByManager(max, page, filterId)

        then:
        thrown(NotAManagerException)

        where:
        employeeRole << [EmployeeRole.HR_ADMIN, EmployeeRole.EMPLOYEE]
    }

    def "getLeavesByManager should throw a ResourceNotFoundException when no employee is associated with the given ID"() {
        given:
        int max = 10
        int page = 1
        Long filterId = 1

        employeeService.getEmployeeById(filterId) >> Optional.empty()

        when:
        leaveApplicationService.getLeavesByManager(max, page, filterId)

        then:
        thrown(ResourceNotFoundException)
    }

    def "getLeavesByManager should return a paginated leave applications of all employees under the direct supervision of the given manager"() {
        given:
        int max = 10
        int passedPage = 1
        int subtractedPage = 0
        Long managerIdFilter = 1
        int totalCount = 2

        Long managerId = 1
        String managerFirstName = "Man"
        String managerLastName = "Doe"

        Long id1 = 1
        String employeeFirstName1 = "John"
        String employeeLastName1 = "Doe"
        LocalDate startDate1 = LocalDate.now()
        LocalDate endDate1 = LocalDate.now()
        int workDays1 = 1
        String reason1 = "Reason 1"
        LeaveApplicationStatus status1 = LeaveApplicationStatus.APPROVED

        Long id2 = 2
        String employeeFirstName2 = "Johny"
        String employeeLastName2 = "Doey"
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
            role >> EmployeeRole.MANAGER
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

        Pageable pageable = PageRequest.of(subtractedPage, max, Sort.by("id"));

        when:
        Page<LeaveApplication> response = leaveApplicationService.getLeavesByManager(max, passedPage, managerIdFilter)

        then:
        1 * employeeService.getEmployeeById(managerId) >> Optional.of(managerObject)
        1 * leaveApplicationRepository.findAllByManager(managerObject, pageable) >> leaveApplicationPage
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
}