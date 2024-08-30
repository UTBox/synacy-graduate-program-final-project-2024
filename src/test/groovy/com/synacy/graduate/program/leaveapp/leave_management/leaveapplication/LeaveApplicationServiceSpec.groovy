package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication

import org.springframework.data.domain.Page
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
        int page = 1;
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

        LeaveApplication leave1 = Mock() {
            id >> id1
            employeeId >> employeeId1
            managerId >> managerId1
            startDate >> startDate1
            endDate >> endDate1
            workDays >> workDays1
            reason >> reason1
            status >> status1
        }
        LeaveApplication leave2 = Mock() {
            id >> id2
            employeeId >> employeeId2
            managerId >> managerId2
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
        Page<LeaveApplication> response = leaveApplicationService.getAllLeaveApplications(max, page)

        then:
        1 * leaveApplicationRepository.findAll(_) >> paginatedLeaves
        totalCount == response.getTotalElements()
        leaveList == response.getContent()
    }
}