package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication

import com.synacy.graduate.program.leaveapp.leave_management.employee.Employee
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import spock.lang.Specification

import java.time.LocalDate


class LeaveApplicationServiceSpec extends Specification {
    LeaveApplicationService leaveApplicationService
    LeaveApplicationRepository leaveApplicationRepository = Mock()
    def setup(){
        leaveApplicationService = new LeaveApplicationService(leaveApplicationRepository)
    }

    def "getAllLeaveApplications should return a paginated list of all leave applications"(){
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

        Employee employee1 = Mock(){
            id >> employeeId1
        }
        Employee employee2 = Mock(){
            id >> employeeId2
        }
        Employee manager1 = Mock(){
            id >> managerId1
        }
        Employee manager2 = Mock(){
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

        Page<LeaveApplication> paginatedLeaves = Mock(){
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
}