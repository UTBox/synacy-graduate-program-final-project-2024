package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication

import com.synacy.graduate.program.leaveapp.leave_management.employee.Employee
import com.synacy.graduate.program.leaveapp.leave_management.employee.EmployeeRole
import com.synacy.graduate.program.leaveapp.leave_management.employee.EmployeeService
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

    def setup(){
        leaveApplicationService = new LeaveApplicationService(leaveApplicationRepository, employeeService)
    }

    def "getLeavesByManager should throw a NotAManagerException when the provided ID has a #employeeRole role"(){
        given:
        int max = 10
        int page = 1
        Long filterId = 1
        Employee notManager = Mock(Employee){
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

    def "getLeavesByManager should return a paginated leave applications of all employees under the direct supervision of the given manager"(){
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

        Pageable pageable = PageRequest.of(subtractedPage, max, Sort.by("id"));

        when:
        Page<LeaveApplication> response = leaveApplicationService.getLeavesByManager(max, passedPage, managerIdFilter)

        then:
        1 * employeeService.getEmployeeById(managerId) >> Optional.of(managerObject)
        1 * leaveApplicationRepository.findAllByManager(managerObject, pageable) >> leaveApplicationPage
        totalCount == response.getTotalElements()
        leaveApplicationList == response.getContent()
    }
}