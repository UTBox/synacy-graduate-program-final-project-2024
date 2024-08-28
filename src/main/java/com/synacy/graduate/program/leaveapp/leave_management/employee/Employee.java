package com.synacy.graduate.program.leaveapp.leave_management.employee;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_sequence")
    @SequenceGenerator(name = "employee_sequence", sequenceName = "employee_sequence", allocationSize = 1)
    private Long id;

    @Setter private String firstName;
    @Setter private String lastName;

    @Setter
    @Enumerated(EnumType.STRING)
    private EmployeeRole role;

    @Setter
    @ManyToOne(targetEntity = Employee.class)
    private Employee Manager;

    @Setter
    private Integer totalLeaves;

    @Setter
    private Integer availableLeaves;

    @Setter
    private Boolean isDeleted;
}
