import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

/**
 * @author Abhishek
 * @version June 2024
 */

public class EmployeeTransaction {

public void addEmployee(int empId, String name, String email, int deptId, int projectId) throws SQLException {
    // SQL statement to insert a new employee into the 'employees' table
    String insertEmployee = "INSERT INTO employees (emp_id, name, email, dept_id) VALUES (?, ?, ?, ?)";
    
    // SQL statement to check if the email already exists in the 'employees' table
    String checkEmail = "SELECT COUNT(*) FROM employees WHERE email = ?";
    
    // SQL statement to update the employee count in the 'departments' table
    String updateDeptCount = "UPDATE departments SET employee_count = employee_count + 1 WHERE dept_id = ?";
    
    // SQL statement to assign the new employee to a project
    String insertProjectAssignment = "INSERT INTO project_assignments (emp_id, project_id) VALUES (?, ?)";
    
    try (Connection conn = DatabaseUtil.getConnection()) {
        // Disable auto-commit to handle transactions manually
        conn.setAutoCommit(false);

        try (PreparedStatement psCheckEmail = conn.prepareStatement(checkEmail);
             PreparedStatement psInsertEmployee = conn.prepareStatement(insertEmployee);
             PreparedStatement psUpdateDeptCount = conn.prepareStatement(updateDeptCount);
             PreparedStatement psInsertProjectAssignment = conn.prepareStatement(insertProjectAssignment)
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

            psInsertEmployee.setInt(1, empId);
            psInsertEmployee.setString(2, name);
            psInsertEmployee.setString(3, email);
            psInsertEmployee.setInt(4, deptId);
            psInsertEmployee.executeUpdate();

            // Update the employee count in the corresponding department
            psUpdateDeptCount.setInt(1, deptId);
            psUpdateDeptCount.executeUpdate();

            // Assign the new employee to the default project

            psInsertProjectAssignment.setInt(1, empId);
            psInsertProjectAssignment.setInt(2, projectId);
            psInsertProjectAssignment.executeUpdate();

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
//        String query = ""
//        		+ "SELECT e.*, pa.project_id as project_id FROM employees e LEFT JOIN "
//        		+ "project_assignments pa ON e.emp_id = pa.emp_id";
//        
        String query = "SELECT e.*, d.dept_name as dept_name, pa.project_id as project_id FROM employees e "
                + "LEFT JOIN departments d ON e.dept_id = d.dept_id "
                + "LEFT JOIN project_assignments pa ON e.emp_id = pa.emp_id";
        
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
}
