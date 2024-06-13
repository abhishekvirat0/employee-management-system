import java.sql.Date;

public class Employee {
    private int empId;
    private String name;
    private String email;
    private String phone;
    private Date dob;
    private String jobTitle;
    private double salary;
    private int deptId;
    private int officeId;
    private int projectId;

    // Constructor
    public Employee(int empId, String name, String email, String phone, Date dob, String jobTitle, double salary, int deptId, int officeId, int projectId) {
        this.empId = empId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.deptId = deptId;
        this.officeId = officeId;
        this.projectId = projectId;
    }

    // Getters and Setters
    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}
