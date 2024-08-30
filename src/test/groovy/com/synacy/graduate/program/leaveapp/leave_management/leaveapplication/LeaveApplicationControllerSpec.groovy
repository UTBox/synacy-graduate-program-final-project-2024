package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication

import com.synacy.graduate.program.leaveapp.leave_management.employee.Employee
import com.synacy.graduate.program.leaveapp.leave_management.employee.EmployeeService
import com.synacy.graduate.program.leaveapp.leave_management.web.PageResponse
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.InvalidOperationException
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.InvalidRequestException
import com.synacy.graduate.program.leaveapp.leave_management.web.apierror.ResourceNotFoundException
import org.springframework.data.domain.Page
import spock.lang.Specification

import java.time.LocalDate

class LeaveApplicationControllerSpec extends Specification {

    LeaveApplicationController leaveApplicationController
    LeaveApplicationService leaveApplicationService = Mock()
    EmployeeService employeeService = Mock()

    def setup(){
        leaveApplicationController = new LeaveApplicationController(leaveApplicationService, employeeService)
    }

    def "getLeaveApplications should return a paginated list of all leave applications"(){
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

        Employee employee1 = Mock(){
            id >> employeeId1
            firstName >> employeeFirstName1
            lastName >> employeeLastName1
        }
        Employee employee2 = Mock(){
            id >> employeeId2
            firstName >> employeeFirstName2
            lastName >> employeeLastName2
        }
        Employee manager1 = Mock(){
            id >> managerId1
            firstName >> managerFirstName1
            lastName >> managerLastName1
        }
        Employee manager2 = Mock(){
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

        Page<LeaveApplication> paginatedLeaves = Mock(){
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
    def "getLeavesByManager should throw an InvalidOperationException when the role of the provided ID is not MANAGER"(){
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

    def "getLeavesByManager should throw an InvalidRequestException when no employee is associated with the given ID"(){
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

    def "getLeavesByManager should return a paginated leave applications of all employees under the direct supervision of the given manager"(){
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

        Employee employee1 = Mock(Employee){
            firstName >> employeeFirstName1
            lastName >> employeeLastName1
        }
        Employee employee2 = Mock(Employee){
            firstName >> employeeFirstName2
            lastName >> employeeLastName2
        }
        Employee managerObject = Mock(Employee){
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
}