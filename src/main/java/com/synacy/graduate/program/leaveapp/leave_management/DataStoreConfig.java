package com.synacy.graduate.program.leaveapp.leave_management;

import com.synacy.graduate.program.leaveapp.leave_management.employee.Employee;
import com.synacy.graduate.program.leaveapp.leave_management.employee.EmployeeRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataStoreConfig {
    @Bean
    public List<Employee> getEmployeesList() {
        List<Employee> employees = new ArrayList<>();

        Employee admin = new Employee(1L, "HR", "ADMIN", EmployeeRole.HR_ADMIN, 0);
        Employee manager = new Employee(2L, "Boss", "Amo", EmployeeRole.MANAGER, 15);

        Employee employee1 = new Employee(3L, "Sean", "Capulong", EmployeeRole.EMPLOYEE, 15);
        Employee employee2 = new Employee(4L, "Julius", "Fabrique", EmployeeRole.EMPLOYEE, 15);
        Employee employee3 = new Employee(5L, "Alwyn", "Dy", EmployeeRole.EMPLOYEE, 15);

        manager.setManager(admin);
        employee1.setManager(manager);
        employee2.setManager(manager);
        employee3.setManager(manager);

        employees.add(admin);
        employees.add(manager);
        employees.add(employee1);
        employees.add(employee2);
        employees.add(employee3);

        return employees;
    }
}
