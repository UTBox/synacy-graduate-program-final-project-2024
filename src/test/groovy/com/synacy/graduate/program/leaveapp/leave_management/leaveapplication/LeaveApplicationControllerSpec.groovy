package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication

import com.synacy.graduate.program.leaveapp.leave_management.employee.Employee
import com.synacy.graduate.program.leaveapp.leave_management.employee.EmployeeService
import com.synacy.graduate.program.leaveapp.leave_management.web.PageResponse
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
}