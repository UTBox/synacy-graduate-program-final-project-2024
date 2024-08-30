package com.synacy.graduate.program.leaveapp.leave_management.leave;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {

}
