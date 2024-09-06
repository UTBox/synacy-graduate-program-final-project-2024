# Employee Leave Management Application
<a id="top"></a>

# API Documentation

---
## Entities

---

- Employee
    - The Employee entity contains the employee details as well as details about their manager as defined by the `@JoinColumn` annotation for the `manager` property
- [Employee endpoints](#employee-endpoints)
```java
@Getter
@NoArgsConstructor
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
    @Column(nullable = false)
    private Boolean isDeleted;

    public Employee(Long id, String firstName, String lastName, EmployeeRole role, Integer totalLeaves) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.totalLeaves = totalLeaves;
        this.availableLeaves = totalLeaves;
        this.isDeleted = false;
    }

    public void deductLeaveBalance(int days) {
        this.availableLeaves = this.availableLeaves - days;
    }

    public void addLeaveBalance(int days) {
        this.availableLeaves = this.availableLeaves + days;
    }

    public String getName(){
        return this.firstName + ' ' + this.lastName;
    }
}
```

- Leave Application
    - The Leave entity contains details about an employee's leave application such as the employee who requested the leave and their respective manager, the number of workdays covered by the leave, and its status.
- [Leave Application endpoints](#leave-application-endpoints)
```java
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
```
<br>

### Page Response Body

---
```java
public record PageResponse<T>(
        long totalCount, 
        int totalPages , 
        int pageNumber, 
        List<T> content) {
}
```
<br>

### Employee Request Bodies

---
#### CreateEmployeeRequest
```java
@Getter
public class CreateEmployeeRequest {

    @NotNull(message = "First name is null.")
    private String firstName;

    @NotNull(message = "Last name is null.")
    private String lastName;

    @NotNull(message = "Role is null.")
    private EmployeeRole role;

    @Min(value = 0, message = "Total Leaves should be at least 0.")
    private Integer totalLeaves;

    private Long managerId;
}
```
<br>

#### UpdateEmployeeRequest
```java
@Getter
public class UpdateEmployeeRequest {
    @NotNull(message = "Total leaves is required")
    @Min(value = 0, message = "Total leaves must not be less than zero.")
    private Integer totalLeaves;
}
```
<br>

### Employee Response Bodies

---
#### EmployeeResponse
```java
@Getter
public class EmployeeResponse {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String fullName;
    private final EmployeeRole role;
    private final ManagerResponse manager;
    private final int totalLeaves;
    private final int availableLeaves;

    public EmployeeResponse(Employee employee) {
        this.id = employee.getId();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.fullName = employee.getFirstName() + " " + employee.getLastName();
        this.role = employee.getRole();
        this.totalLeaves = employee.getTotalLeaves();
        this.availableLeaves = employee.getAvailableLeaves();

        if (employee.getManager() == null) {
            this.manager = null;
        } else {
            this.manager = new ManagerResponse(employee.getManager());
        }
    }
}
```
<br>

#### EmployeeListResponse
```java
@Getter
public class EmployeeListResponse {
    private Long id;
    private String name;
    private EmployeeRole role;

    EmployeeListResponse(Employee employee){
        this.id = employee.getId();
        this.name = employee.getName();
        this.role = employee.getRole();
    }
}
```
<br>

#### ManagerResponse
```java
@Getter
public class ManagerResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private EmployeeRole role;

    ManagerResponse(Employee manager){
        this.id = manager.getId();
        this.firstName = manager.getFirstName();
        this.lastName = manager.getLastName();
        this.role = manager.getRole();
    }
}
```
<br>

## Employee Endpoints

---
### Get Employees Paginated

#### Request
`GET` `/api/v1/employee`

##### Request Parameters
| Request Parameter |  Type   |  Required  |  Default Value  | Description                                  |
|-------------------|:-------:|:----------:|:---------------:|----------------------------------------------|
| max               | Integer |    true    |        2        | Maximum number of results displayed per page |
| page              | Integer |    true    |        1        | Page number                                  |

##### Request Body
No Request Body

#### Response
Status Code: `200 OK`

##### Response Body
###### [PageResponse](#page-response-body)[\<EmployeeResponse\>](#EmployeeResponse)
Example:
```json
{
  "totalCount": 11,
  "totalPages": 6,
  "pageNumber": 1,
  "content": [
    {
      "id": 1,
      "firstName": "HR",
      "lastName": "ADMIN",
      "fullName": "HR ADMIN",
      "role": "HR_ADMIN",
      "manager": null,
      "totalLeaves": 0,
      "availableLeaves": 0
    },
    {
      "id": 2,
      "firstName": "Boss",
      "lastName": "Amo",
      "fullName": "Boss Amo",
      "role": "MANAGER",
      "manager": {
        "id": 1,
        "firstName": "HR",
        "lastName": "ADMIN",
        "role": "HR_ADMIN"
      },
      "totalLeaves": 15,
      "availableLeaves": 0
    }
  ]
}
```

---
### Get Employees List

#### Request
`GET` `/api/v1/list/employee`

##### Request Parameters
| Request Parameter |  Type  | Required | Description      |
|-------------------|:------:|:--------:|------------------|
| name              | String |  false   | Name of employee |

##### Request Body
No Request Body

#### Response
Status Code: `200 OK`

##### Response Body
###### List[\<EmployeeListResponse\>](#EmployeeListResponse)
Example:
```json
[
    {
        "id": 1,
        "name": "HR ADMIN",
        "role": "HR_ADMIN"
    },
    {
        "id": 2,
        "name": "Boss Amo",
        "role": "MANAGER"
    },
    {
        "id": 3,
        "name": "Sean Capulong",
        "role": "EMPLOYEE"
    },
    {
        "id": 4,
        "name": "Julius Fabrique",
        "role": "EMPLOYEE"
    },
    {
        "id": 5,
        "name": "Alwyn Dy",
        "role": "EMPLOYEE"
    }
]
```

---
### Get Managers List

#### Request
`GET` `/api/v1/list/manager`

##### Request Parameters
| Request Parameter |  Type  | Required | Description     |
|-------------------|:------:|:--------:|-----------------|
| name              | String |  false   | Name of manager |

##### Request Body
No Request Body

#### Response
Status Code: `200 OK`

##### Response Body
###### List[\<ManagerResponse\>](#ManagerResponse)
Example:
```json
[
    {
        "id": 1,
        "firstName": "HR",
        "lastName": "ADMIN",
        "role": "HR_ADMIN"
    },
    {
        "id": 2,
        "firstName": "Boss",
        "lastName": "Amo",
        "role": "MANAGER"
    },
    {
        "id": 6,
        "firstName": "Alice",
        "lastName": "Johnson",
        "role": "MANAGER"
    }
]
```

---
### Get Employee

#### Request
`GET` `/api/v1/employee/{id}`

##### Request Parameters
| Request Parameter | Type | Required | Description |
|-------------------|:----:|:--------:|-------------|
| id                | Long |   true   | Employee ID |

##### Request Body
No Request Body

#### Response
Status Code: `200 OK`

##### Response Body
###### [EmployeeResponse](#EmployeeResponse)
Example:
```json
{
    "id": 3,
    "firstName": "Sean",
    "lastName": "Capulong",
    "fullName": "Sean Capulong",
    "role": "EMPLOYEE",
    "manager": {
        "id": 2,
        "firstName": "Boss",
        "lastName": "Amo",
        "role": "MANAGER"
    },
    "totalLeaves": 15,
    "availableLeaves": 2
}
```

---
### Create Employee

#### Request
`POST` `/api/v1/employee`

##### Request Body
###### [CreateEmployeeRequest](#CreateEmployeeRequest)
Example:
```json
{
    "firstName": "John",
    "lastName": "Cena",
    "role": "EMPLOYEE",
    "totalLeaves": 15,
    "managerId": 2
}
```

#### Response
Status Code: `201 Created`

##### Response Body
###### [EmployeeResponse](#EmployeeResponse)
Example:
```json
{
    "id": 12,
    "firstName": "John",
    "lastName": "Cena",
    "fullName": "John Cena",
    "role": "EMPLOYEE",
    "manager": {
        "id": 2,
        "firstName": "Boss",
        "lastName": "Amo",
        "role": "MANAGER"
    },
    "totalLeaves": 15,
    "availableLeaves": 15
}
```

---
### Update Employee

#### Request
`PUT` `/api/v1/employee/{id}`

##### Request Parameters
| Request Parameter | Type | Required | Description |
|-------------------|:----:|:--------:|-------------|
| id                | Long |   true   | Employee ID |

##### Request Body
###### [UpdateEmployeeRequest](#UpdateEmployeeRequest)
Example:
```json
{
  "totalLeaves": 15
}
```

#### Response
Status Code: `200 OK`

##### Response Body
###### [EmployeeResponse](#EmployeeResponse)
Example:
```json
{
    "id": 3,
    "firstName": "Sean",
    "lastName": "Capulong",
    "fullName": "Sean Capulong",
    "role": "EMPLOYEE",
    "manager": {
        "id": 2,
        "firstName": "Boss",
        "lastName": "Amo",
        "role": "MANAGER"
    },
    "totalLeaves": 15,
    "availableLeaves": 2
}
```

<br>

### Leave Application Request Bodies

---
#### CreateLeaveApplicationRequest
```java
@Getter
public class CreateLeaveApplicationRequest {
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
}
```
<br>

#### UpdateLeaveApplicationRequest
```java
@Getter
public class UpdateLeaveApplicationRequest {
    @NotNull(message = "Leave status is required")
    private LeaveApplicationStatus status;
}
```
<br>

#### ManagerialLeaveApplicationResponse
```java
@Getter
public class ManagerialLeaveApplicationResponse {
    private final Long id;
    private final String employeeName;
    private final String managerName;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Integer workDays;
    private final String reason;
    private final LeaveApplicationStatus status;

    public ManagerialLeaveApplicationResponse(LeaveApplication leaveApplication) {
        this.id = leaveApplication.getId();
        this.employeeName = leaveApplication.getEmployee().getName();
        this.managerName = leaveApplication.getManager().getName();
        this.startDate = leaveApplication.getStartDate();
        this.endDate = leaveApplication.getEndDate();
        this.workDays = leaveApplication.getWorkDays();
        this.reason = leaveApplication.getReason();
        this.status = leaveApplication.getStatus();
    }
}
```
<br>

#### EmployeeLeaveApplicationResponse
```java
@Getter
public class EmployeeLeaveApplicationResponse {
    private final Long id;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Integer workDays;
    private final String reason;
    private final LeaveApplicationStatus status;

    public EmployeeLeaveApplicationResponse(LeaveApplication leaveApplication) {
        this.id = leaveApplication.getId();
        this.startDate = leaveApplication.getStartDate();
        this.endDate = leaveApplication.getEndDate();
        this.workDays = leaveApplication.getWorkDays();
        this.reason = leaveApplication.getReason();
        this.status = leaveApplication.getStatus();
    }
}
```
<br>

## Leave Application Endpoints

---

### Get Managerial Leave Applications

#### Request
`GET` `/api/v1/leave`

##### Request Parameters
| Request Parameter |  Type   | Required | Default Value | Description                                  |
|-------------------|:-------:|:--------:|:-------------:|----------------------------------------------|
| max               | Integer |   true   |       2       | Maximum number of results displayed per page |
| page              | Integer |   true   |       1       | Page number                                  |
| manager           |  Long   |  false   |       -       | Manager ID                                   |
| status            |  Enum   |   true   |       -       | Status of the leave application              |

##### Request Body
No Request Body

#### Response
Status Code: `200 OK`

##### Response Body
###### [PageResponse](#page-response-body)[\<ManagerialLeaveApplicationResponse\>](#ManagerialLeaveApplicationResponse)
Example:
```json
{
    "totalCount": 11,
    "totalPages": 6,
    "pageNumber": 1,
    "content": [
        {
            "id": 1,
            "employeeName": "Sean Capulong",
            "managerName": "Boss Amo",
            "startDate": "2024-09-09",
            "endDate": "2024-09-11",
            "workDays": 3,
            "reason": "Vacation",
            "status": "PENDING"
        },
        {
            "id": 2,
            "employeeName": "Julius Fabrique",
            "managerName": "Boss Amo",
            "startDate": "2024-09-09",
            "endDate": "2024-09-10",
            "workDays": 2,
            "reason": "Vacation",
            "status": "PENDING"
        }
    ]
}
```

---
### Get Leave by Employee

#### Request
`GET` `/api/v1/leave/employee/{id}`

##### Request Parameters
| Request Parameter |  Type   | Required | Default Value | Description                                  |
|-------------------|:-------:|:--------:|:-------------:|----------------------------------------------|
| max               | Integer |   true   |       2       | Maximum number of results displayed per page |
| page              | Integer |   true   |       1       | Page number                                  |
| id                |  Long   |   true   |       -       | Employee ID                                  |

##### Request Body
No Request Body

#### Response
Status Code: `200 OK`

##### Response Body
###### [PageResponse](#page-response-body)[\<EmployeeLeaveApplicationResponse\>](#EmployeeLeaveApplicationResponse)
Example:
```json
{
    "totalCount": 2,
    "totalPages": 1,
    "pageNumber": 1,
    "content": [
        {
            "id": 1,
            "startDate": "2024-09-09",
            "endDate": "2024-09-11",
            "workDays": 3,
            "reason": "Vacation",
            "status": "PENDING"
        },
        {
            "id": 11,
            "startDate": "2024-09-30",
            "endDate": "2024-10-11",
            "workDays": 10,
            "reason": "Vacation",
            "status": "PENDING"
        }
    ]
}
```

---
### Create Leave Application

#### Request
`POST` `/api/v1/leave`

##### Request Body
###### [CreateLeaveApplicationRequest](#CreateLeaveApplicationRequest)
Example:
```json
{
    "employeeId": 10,
    "startDate": "2024-10-26",
    "endDate": "2024-10-30",
    "reason": "Vacation"
}
```

#### Response
Status Code: `201 Created`

##### Response Body
###### [EmployeeLeaveApplicationResponse](#EmployeeLeaveApplicationResponse)
Example:
```json
{
    "id": 12,
    "startDate": "2024-10-26",
    "endDate": "2024-10-30",
    "workDays": 3,
    "reason": "Vacation",
    "status": "PENDING"
}
```

---
### Update Leave Application

#### Request
`PUT` `/api/v1/leave/{id}`

##### Request Parameters
| Request Parameter | Type | Required | Description |
|-------------------|:----:|:--------:|-------------|
| id                | Long |   true   | Employee ID |

##### Request Body
###### [UpdateLeaveApplicationRequest](#UpdateLeaveApplicationRequest)
Example:
```json
{
    "status": "APPROVED"
}
```

#### Response
Status Code: `200 OK`

##### Response Body
###### [EmployeeLeaveApplicationResponse](#EmployeeLeaveApplicationResponse)
Example:
```json
{
    "id": 12,
    "startDate": "2024-10-26",
    "endDate": "2024-10-30",
    "workDays": 3,
    "reason": "Vacation",
    "status": "APPROVED"
}
```

---
### Cancel Leave Application

#### Request
`DELETE` `/api/v1/leave/{id}`

##### Request Parameters
| Request Parameter | Type | Required | Description |
|-------------------|:----:|:--------:|-------------|
| id                | Long |   true   | Employee ID |

##### Request Body
No Request Body

#### Response
Status Code: `204 No Content`

##### Response Body
No Response Body

---
[Back to Employee endpoints](#employee-endpoints)

[Back to Leave Application endpoints](#leave-application-endpoints)

[Back to top](#top)