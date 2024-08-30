package com.synacy.graduate.program.leaveapp.leave_management.leave;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity(name = "leave_application")
public class LeaveApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leave_application_sequence")
    @SequenceGenerator(name = "leave_application_sequence", sequenceName = "leave_application_sequence", allocationSize = 1)
    private Long id;

    @Setter
    @Column(nullable = false)
    private Long employeeId;

    @Setter
    @Column
    private Long managerId;

    @Setter
    @Column(nullable = false)
    private LocalDate startDate;

    @Setter
    @Column(nullable = false)
    private LocalDate endDate;

    @Setter
    @Column(nullable = false)
    private Integer workDays;

    @Setter
    @Column(nullable = false)
    private String reason;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    public LeaveApplication(Long id, Long employeeId, Long managerId, LocalDate startDate, LocalDate endDate, Integer workDays, String reason) {
        this.id = id;
        this.employeeId = employeeId;
        this.managerId = managerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.workDays = workDays;
        this.reason = reason;
        this.status = LeaveStatus.PENDING;
    }
}
