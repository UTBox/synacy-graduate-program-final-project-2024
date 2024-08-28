package com.synacy.graduate.program.leaveapp.leave_management.employee;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_sequence")
    @SequenceGenerator(name = "employee_sequence", sequenceName = "employee_sequence", allocationSize = 1)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String firstName;

    @Setter
    @Column(nullable = false)
    private String lastName;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EmployeeRole role;

    @Setter
    @ManyToOne(targetEntity = Employee.class)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @Setter
    @Column(nullable = false)
    private Integer totalLeaves;

    @Setter
    @Column(nullable = false)
    private Integer availableLeaves;

    @Setter
    private Boolean isDeleted;

    public Employee() {
    }

    public Employee(String firstName, String lastName, EmployeeRole role, Employee manager, Integer totalLeaves) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.manager = manager;
        this.totalLeaves = totalLeaves;
        this.availableLeaves = totalLeaves;
    }
}
