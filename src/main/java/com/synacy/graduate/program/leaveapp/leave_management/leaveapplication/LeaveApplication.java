package com.synacy.graduate.program.leaveapp.leave_management.leaveapplication;

import com.synacy.graduate.program.leaveapp.leave_management.employee.Employee;
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
    @ManyToOne(targetEntity = Employee.class)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    private Employee employee;

    @Setter
    @ManyToOne(targetEntity = Employee.class)
    @JoinColumn(name = "manager_id", referencedColumnName = "id", nullable = false)
    private Employee manager;

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
    private LeaveApplicationStatus status;

    public LeaveApplication(Long id, Employee employee, Employee manager, LocalDate startDate, LocalDate endDate, Integer workDays, String reason) {
        this.id = id;
        this.employee = employee;
        this.manager = manager;
        this.startDate = startDate;
        this.endDate = endDate;
        this.workDays = workDays;
        this.reason = reason;
        this.status = LeaveApplicationStatus.PENDING;
    }

    void cancelLeave() {
        this.status = LeaveApplicationStatus.CANCELLED;
    }
}
