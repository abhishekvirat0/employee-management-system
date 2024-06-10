import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

/**
 * @author Abhishek

 * @version June 2024
 */

public class EmployeeTransaction {

public void addEmployee(int empId, String name, String email, String phone, Date dob, String jobTitle, double salary, int deptId, int officeId, int projectId) throws SQLException {

    // SQL statement to insert a new employee into the 'employees' table
    String insertEmployee = "INSERT INTO employees ("
            + "emp_id, "
            + "name, "
            + "email, "
            + "phone, "
            + "dob, "
            + "job_title, "
            + "salary, "
            + "hire_date, "
            + "dept_id, "
            + "office_id"
            + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    // SQL statement to check if the email already exists in the 'employees' table
    String checkEmail = "SELECT COUNT(*) FROM employees WHERE email = ?";
    
    // SQL statement to add offices, 
    
    // SQL statement to assign the new employee to a project
    String insertProjectAssignment = "INSERT INTO project_assignments (emp_id, project_id) VALUES (?, ?)";
    
    // SQL statement to randomly assign a supervisor to the new employee
    String getRandomSupervisor = "SELECT emp_id FROM employees ORDER BY RAND() LIMIT 1";
    
    // SQL statement to insert into the 'supervisors' table
    String insertSupervisor = "INSERT INTO supervisors (emp_id, supervisor_id) VALUES (?, ?)";

    try (Connection conn = DatabaseUtil.getConnection()) {
        // Disable auto-commit to handle transactions manually
        conn.setAutoCommit(false);

        try (PreparedStatement psCheckEmail = conn.prepareStatement(checkEmail);
             PreparedStatement psInsertEmployee = conn.prepareStatement(insertEmployee);
             PreparedStatement psInsertProjectAssignment = conn.prepareStatement(insertProjectAssignment);
        	 PreparedStatement psGetRandomSupervisor = conn.prepareStatement(getRandomSupervisor);
             PreparedStatement psInsertSupervisor = conn.prepareStatement(insertSupervisor)
            ) {

            // Check if the email already exists in the database
            psCheckEmail.setString(1, email);
            ResultSet rs = psCheckEmail.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // If the email exists, rollback the transaction and throw an exception
                conn.rollback();
                throw new SQLException("Email already exists.");
            }

            // Insert the new employee into the 'employees' table
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            
//            System.out.println("asdasdas " + empId + " " + name + " " + email + " " + phone + " " + dob + " " + jobTitle + " " + salary + " " + deptId + " " + officeId + " " + projectId);

            psInsertEmployee.setInt(1, empId);
            psInsertEmployee.setString(2, name);
            psInsertEmployee.setString(3, email);
            psInsertEmployee.setString(4, phone);
            psInsertEmployee.setDate(5, dob);
            psInsertEmployee.setString(6, jobTitle);
            psInsertEmployee.setDouble(7, salary);
            psInsertEmployee.setTimestamp(8, currentTimestamp);
            psInsertEmployee.setInt(9, deptId);
            psInsertEmployee.setInt(10, officeId);
            
            psInsertEmployee.executeUpdate();

            // Assign the new employee to the default project

            psInsertProjectAssignment.setInt(1, empId);
            psInsertProjectAssignment.setInt(2, projectId);
            psInsertProjectAssignment.executeUpdate();

            // Get a random supervisor from the already inserted employees
            ResultSet rsSupervisor = psGetRandomSupervisor.executeQuery();
            if (rsSupervisor.next()) {
                int supervisorId = rsSupervisor.getInt("emp_id");
                
                // Insert the new employee into the 'supervisors' table with the assigned supervisor
                psInsertSupervisor.setInt(1, empId);
                psInsertSupervisor.setInt(2, supervisorId);
                psInsertSupervisor.executeUpdate();
            } else {
                conn.rollback();
                throw new SQLException("No existing employees found to assign as a supervisor.");
            }
            
            // Commit the transaction if everything is successful
            conn.commit();
        } catch (SQLException e) {
        	if (e.getErrorCode() == 1062) { // MySQL error code for duplicate entry
                JOptionPane.showInputDialog(null,"User already Exist with this Id");
        	}
            // Rollback the transaction in case of any exception and throw the exception
            conn.rollback();
            throw e;
        }
    }
}

public void updateEmployeeEmail(int empId, String newEmail) throws SQLException {
        String updateEmail = "UPDATE employees SET email = ? WHERE emp_id = ?";
        String checkEmail = "SELECT COUNT(*) FROM employees WHERE email = ?";

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psCheckEmail = conn.prepareStatement(checkEmail);
                 PreparedStatement psUpdateEmail = conn.prepareStatement(updateEmail)) {

                psCheckEmail.setString(1, newEmail);
                ResultSet rs = psCheckEmail.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    conn.rollback();
                    throw new SQLException("Email already exists.");
                }

                psUpdateEmail.setString(1, newEmail);
                psUpdateEmail.setInt(2, empId);
                psUpdateEmail.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void deleteEmployee(int empId) throws SQLException {
        String checkProjects = "SELECT COUNT(*) FROM project_assignments WHERE emp_id = ?";
        String deleteEmployee = "DELETE FROM employees WHERE emp_id = ?";
        String updateDeptCount = "UPDATE departments SET employee_count = employee_count - 1 WHERE dept_id = (SELECT dept_id FROM employees WHERE emp_id = ?)";

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psCheckProjects = conn.prepareStatement(checkProjects);
                 PreparedStatement psDeleteEmployee = conn.prepareStatement(deleteEmployee);
                 PreparedStatement psUpdateDeptCount = conn.prepareStatement(updateDeptCount)) {

                psCheckProjects.setInt(1, empId);
                ResultSet rs = psCheckProjects.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    conn.rollback();
                    throw new SQLException("Employee is assigned to active projects.");
                }

                psUpdateDeptCount.setInt(1, empId);
                psUpdateDeptCount.executeUpdate();

                psDeleteEmployee.setInt(1, empId);
                psDeleteEmployee.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void transferEmployee(int empId, int newDeptId) throws SQLException {
        String updateEmployeeDept = "UPDATE employees SET dept_id = ? WHERE emp_id = ?";
        String updateOldDeptCount = "UPDATE departments SET employee_count = employee_count - 1 WHERE dept_id = (SELECT dept_id FROM employees WHERE emp_id = ?)";
        String updateNewDeptCount = "UPDATE departments SET employee_count = employee_count + 1 WHERE dept_id = ?";

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psUpdateEmployeeDept = conn.prepareStatement(updateEmployeeDept);
                 PreparedStatement psUpdateOldDeptCount = conn.prepareStatement(updateOldDeptCount);
                 PreparedStatement psUpdateNewDeptCount = conn.prepareStatement(updateNewDeptCount)) {

                psUpdateOldDeptCount.setInt(1, empId);
                psUpdateOldDeptCount.executeUpdate();

                psUpdateEmployeeDept.setInt(1, newDeptId);
                psUpdateEmployeeDept.setInt(2, empId);
                psUpdateEmployeeDept.executeUpdate();

                psUpdateNewDeptCount.setInt(1, newDeptId);
                psUpdateNewDeptCount.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public ResultSet getAllEmployees() throws SQLException {
        
        String query = "SELECT e.*, d.dept_name as dept_name, p.project_name as project_name FROM employees e "
                + "LEFT JOIN departments d ON e.dept_id = d.dept_id "
                + "LEFT JOIN project_assignments pa ON e.emp_id = pa.emp_id "
                + "LEFT JOIN projects p ON pa.project_id = p.project_id";
        
        Connection conn = DatabaseUtil.getConnection();
        PreparedStatement ps = conn.prepareStatement(query);
        return ps.executeQuery();
    }
    
    public Map<String, Integer> fetchDepartment() {
    	Map<String, Integer> departments = new HashMap<>();

        Statement stmt = null;
        ResultSet rs = null;

        try(Connection conn = DatabaseUtil.getConnection()) {

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT dept_id, dept_name FROM departments");

            // Retrieve department names from the result set and add them to the list
            while (rs.next()) {
            	int departmentId = rs.getInt("dept_id");
                String departmentName = rs.getString("dept_name");
               
				departments.put(departmentName, departmentId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return departments;
    }
    
    public Map<String, Integer> fetchProject() {
    	Map<String, Integer> projects = new HashMap<>();

        Statement stmt = null;
        ResultSet rs = null;

        try(Connection conn = DatabaseUtil.getConnection()) {

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT project_id, project_name FROM projects");

            // Retrieve department names from the result set and add them to the list
            while (rs.next()) {
            	int projectId = rs.getInt("project_id");
                String projectName = rs.getString("project_name");
               
                projects.put(projectName, projectId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return projects;
    }
    
    public Map<String, Integer> fetchOffice() {
    	Map<String, Integer> offices = new HashMap<>();

        Statement stmt = null;
        ResultSet rs = null;

        try(Connection conn = DatabaseUtil.getConnection()) {

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT office_id, location FROM offices");

            // Retrieve department names from the result set and add them to the list
            while (rs.next()) {
            	int officeId = rs.getInt("office_id");
                String location = rs.getString("location");
               
                offices.put(location, officeId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return offices;
    }
    
    // TODO: @omkar add 5 methods using JOIN to fetch data
    
    // TODO: add data in supervisor table on inserting an employee, randomly assign an employee as a supervisor 
    
}
